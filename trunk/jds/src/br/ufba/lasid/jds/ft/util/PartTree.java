/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.ft.util;

import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.pages.PageIndexList;
import br.ufba.lasid.jds.security.util.XSecurity;
import br.ufba.lasid.jds.util.JDSUtility;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.MRU;
import jdbm.helper.Tuple;
import jdbm.recman.CacheRecordManager;

/**
 *
 * @author aliriosa
 */
public class PartTree implements Serializable{

    private static final long serialVersionUID = 888337485696555762L;
    
    protected int LEVELS =  4;
    protected int ORDER = 256;
    protected String checkpointStoreID = "";
    protected PartTable table;

    public PartTree(){}
    
    public PartTree(String checkpointStoreID, int cacheSize, int LEVELS, int ORDER) throws Exception{
        this.LEVELS = LEVELS;
        this.ORDER = ORDER;
        table = new PartTable(checkpointStoreID, cacheSize);
    }

    public PartTable getPartTable(){
        return this.table;
    }

    public static boolean isPage(int ORDER, PartEntry part){
        return isPage((int)part.level, ORDER);
    }

    public static boolean isPage(int ORDER, long level){
        return level == ORDER;
    }
    
    public static long getRecordindex(int ORDER, long lpart, long ipart){
            long recindex = 1;

            for(int L = 0; L <= lpart - 1; L++){
                recindex += (long)Math.pow(ORDER, L);
            }

            return recindex + ipart;
    }

    public class PartKey implements Serializable{
        long partIndex = 0;
        long partLevel = 0;
        long checkpoint = 0;
        
        public PartKey(){}

        public PartKey(long level, long index, long checkpoint){
            this.partIndex = index;
            this.partLevel = level;
            this.checkpoint = checkpoint;
        }

        public PartKey(long level, long index){
            this.partIndex = index;
            this.partLevel = level;
        }

        public long getPartIndex() { return partIndex; }
        public void setPartIndex(long index) { this.partIndex = index; }
        public long getPartLevel() { return partLevel; }
        public void setPartLevel(long level) { this.partLevel = level; }
        
        public long getRecordIndex(){
            return getRecordindex(ORDER, this.partLevel, this.partIndex);
        }

        public long getCheckpointID() { return checkpoint; }
        public void setCheckpointID(long checkpointID) { this.checkpoint = checkpointID; }
    }

    public class PartEntry implements Serializable{

        private static final long serialVersionUID = -5174222324310572813L;

        long      index =  0;
        long      level =  0;
        long checkpoint =  0;
        String   digest = "";

        public PartEntry(){}
        
        public PartEntry(long part, long level, long checkpoint, String digest){
            this.index = part;             this.level = level;
            this.checkpoint = checkpoint; this.digest = digest;
        }
        public PartEntry(long part, long level, long checkpoint){
            this(part, level, checkpoint, "");
        }

        public PartEntry(long part, long level){
            this(part, level, 0, "");
        }

        public PartEntry(long part){
            this(part, 0, 0, "");
        }

        public long getOldestSubpart(){
            return (getYoungestSubpart() + 1) - ORDER;
        }

        public long getYoungestSubpart(){
            return (index + 1) * ORDER - 1;
        }
        
        public long getPartCheckpoint() { return checkpoint;}
        public void setCheckpoint(long checkpoint) { this.checkpoint = checkpoint;}
        public String getDigest() { return digest; }
        public void setDigest(String digest) { this.digest = digest;}
        public long getPartIndex( ) { return index; }
        public void setIndex(long index ) { this.index = index; }
        public long getPartLevel( ) { return level; }
        public void setLevel(long level ) { this.level = level; }

        @Override
        public String toString() {
            return "PartEntry{" +
                        "LEVEL = " + level + ", " +
                        "INDEX = " + index + ", " +
                        "CHECKPOINT = " + checkpoint + ", " +
                        "DIGEST = " + digest +
                    '}';
        }



    }

    public static PartEntry newPartEntry(
         int TREEORDER,
         int TREELEVELS,
         long partindex,
         long partlevel,
         long partcheckpoint,
         String partdigest
    ){
        PartTree partree = new PartTree();
        partree.LEVELS = TREELEVELS;
        partree.ORDER = TREEORDER;
        PartEntry part = partree.new PartEntry(partindex, partlevel, partcheckpoint, partdigest);

        return part;
        
    }
    public class PartTable  implements Serializable, IParttable{
        private static final long serialVersionUID = -7049378239504881330L;

        transient RecordManager recman;
        transient BTree btree;
        String checkpointStorageID;
        String btreename = "__checkpoint__btree";
        long recid;
        int cacheSize;
        
        public PartTable(String checkpointStorageID, int cacheSize) throws Exception{
            this.checkpointStorageID = checkpointStorageID;
            this.cacheSize = cacheSize;
            btreename += checkpointStorageID;
        }

        class PartEntryKeyRecordIndexComparator implements Serializable, Comparator<Long>{
            private static final long serialVersionUID = -8872656148604795139L;
            
            public int compare(Long o1, Long o2) {
                Long l1 = o1;
                Long l2 = o2;
                return l1.compareTo(l2);
            }
        }

        class PartEntryKeyCheckpointIDComparator implements Serializable, Comparator<Long>{

            private static final long serialVersionUID = -6771079375851505862L;
            
            public int compare(Long o1, Long o2) {
                Long c1 = o1;
                Long c2 = o2;
                return c1.compareTo(c2);
            }
        }
        public void open() throws Exception{
            open(true);
        }

        public void open(boolean byRecordIndex) throws Exception{

            recman = new CacheRecordManager(RecordManagerFactory.createRecordManager(checkpointStorageID), new MRU(cacheSize));
            
            recid = recman.getNamedObject(btreename);

            if(recid    != 0){
                btree = BTree.load(recman, recid);
                JDSUtility.debug(
                    "[PartTable] Reloading existing B+Tree (" + checkpointStorageID + ") " +
                    "with " + btree.size() + " records"
                 );
            }else{

//                Comparator<Long> comparator = new Comparator<Long>() {
//                    public int compare(Long o1, Long o2) {
//                        return o1.compareTo(o2);
//                    }
//                };
                if(byRecordIndex){
                    btree = BTree.createInstance(recman, new PartEntryKeyRecordIndexComparator());
                }else{
                    btree = BTree.createInstance(recman, new PartEntryKeyCheckpointIDComparator());
                }

                recman.setNamedObject(btreename, btree.getRecid());

                JDSUtility.debug(
                    "[PartTable] Created a B+Tree (" + checkpointStorageID + ") " +
                    "with " + btree.size() + " records"
                 );
            }            
        }

        public synchronized PartEntry getPart(long lpart, long ipart) throws Exception{
            return getPart(new PartKey(lpart, ipart));
        }
        public synchronized PartEntry get(Long recid) throws Exception{
            PartEntry entry = null;

            if(recid != null && recid >= 0){

                entry = (PartEntry)btree.find(recid);
                
                if(entry != null){
                    PartKey key = new PartKey(entry.level, entry.index);
                    if(key.getRecordIndex() == recid){
                        return entry;
                    }
                }
            }

            return entry;            
        }
        public synchronized PartEntry getPart(PartKey key) throws Exception{
            PartEntry entry = null;
            if(key != null){
                entry = (PartEntry) btree.find(key.getRecordIndex());
                if(entry != null && entry.index == key.partIndex && entry.level == key.partLevel){
                    return entry;
                }
            }

            return entry;
        }

        public PartList getFamily(long lpart, long ipart, long minage) throws Exception{
            PartList members = new PartList();

            if(lpart <= LEVELS){
                PartEntry member = get(PartTree.getRecordindex(ORDER, lpart, ipart));

                if(member != null){
                    if(member.checkpoint > minage && !members.contains(member)){
                        members.add(member);

                        PartList subparts = getSubparts(lpart, ipart);
                        for(PartEntry subpart : subparts){
                            if(subpart != null && subpart.checkpoint > minage && !members.contains(subpart) ){
                                long isubpart = subpart.index;
                                long lsubpart = subpart.level;
                                if(lsubpart + 1 <= LEVELS){
                                    PartList succs = getFamily(lsubpart, isubpart, minage);
                                    members.addAll(succs);
                                }else{
                                    members.add(subpart);
                                }//if there're more sons
                            }//if subpart has sons
                        }
                    }
                }                
            }

            return members;
        }

        public synchronized PartList getSubparts(long lpart, long ipart) throws Exception{
            PartList subparts = new PartList();
            PartKey partkey = new PartKey(lpart, ipart);
            PartEntry entry = getPart(partkey);

            if( entry!= null ){
                long lsubpart = lpart + 1;
                long ioldest   = entry.getOldestSubpart();
                long iyoungest = entry.getYoungestSubpart();
                if(lsubpart <= LEVELS){                    
                    for(long isubpart = ioldest; isubpart < iyoungest; isubpart++){
                        PartKey subpartkey = new PartKey(lsubpart, isubpart);
                        PartEntry subpart  = getPart(subpartkey);
                        if(subpart != null && !subparts.contains(subpart))
                            subparts.add(subpart);                       
                    }//end for oldest (lowest index) to youngest (biggest index) son
                }//if belongs to the tree
            }//if is a valid entry
            return subparts;
        }

        public synchronized PartEntry getRootEntry() throws Exception{
            PartKey key = new PartKey(0, 0);

            return getPart(key);
        }

        public synchronized  void commit() throws Exception{
            recman.commit();
        }
        
        public synchronized void put(PartKey key, PartEntry value) throws Exception{
            if(value != null && key != null){
                btree.insert(key.getRecordIndex(), value, true);
                commit();
            }
        }

        public synchronized PartEntry put(Long recid, PartEntry value) throws Exception{
            if(value != null && recid != null){
                btree.insert(recid, value, true);
                commit();
            }
            return value;
        }

        public synchronized void remove(PartKey key) throws Exception{
            if(key != null){
                btree.remove(key.getRecordIndex());
                commit();
            }
        }

        public synchronized void remove(Long recid) throws Exception{
            if(recid != null){
                btree.remove(recid);
                commit();
            }            
        }
        public synchronized PartEntry findGreaterOrEqual(PartKey key) throws Exception{
            
            Tuple tuple = btree.findGreaterOrEqual(key.getRecordIndex());
            
            if(tuple != null){
                return (PartEntry)tuple.getValue();
            }
            
            return  null;
        }

        public synchronized void close() throws Exception{
            recman.close();
        }
        
    }
    
    protected void doStampPageParts(PageIndexList iPagesUsed, long checkpoint, IMemory storage)
            throws Exception
    {
        PartKey key = new PartKey(0, 0);
        
        for(Long ipage : iPagesUsed){
            
            long ipart = ipage;
            
            key.partIndex = ipart; key.partLevel = LEVELS;

            PartEntry entry = table.getPart(key);

            if(entry == null){
                entry = new PartEntry(ipart, LEVELS);
                //table.put(key, entry);
            }

            entry.checkpoint = checkpoint;
            entry.digest = leafDigest(entry, storage.readPage(ipart));
            key.checkpoint = checkpoint;
            table.put(key, entry);
            JDSUtility.debug("<" + entry + ">");

        }        
    }
    
    protected void doStampMetaParts(PageIndexList iPagesUsed, long checkpoint, IMemory storage)
            throws Exception
    {
        PartKey key = new PartKey(0, 0);

        for(Long ipage : iPagesUsed){

            long whocare = ipage + 1;

            for(int level = LEVELS-1; level >= 0; level--){

                long nparts = (long) Math.pow(ORDER, LEVELS - level); 

                whocare = (long)Math.ceil((double)(ipage + 1)/(double)nparts);

                long ipart = whocare - 1;

                key = new PartKey(level, ipart, checkpoint); 

                PartEntry entry = table.getPart(key);

                if(entry == null){
                    entry = new PartEntry(ipart, level);
                    //table.put(key, entry);
                }

                entry.checkpoint = checkpoint;

                entry.digest = PartTree.subpartsDigest(this.table, entry, ORDER, LEVELS);

                table.put(key, entry);
                JDSUtility.debug("<" + entry + ">");

            }
        }
    }

    public static String subpartsDigest(IParttable table, PartEntry entry, int ORDER, int LEVELS) throws Exception{

            long istartsubpart = entry.getOldestSubpart();    //whocare * ORDER;
            long ifinalsubpart = entry.getYoungestSubpart();  //ifinal - ORDER;

            long lsubpart = entry.level + 1;

            BigInteger b = new BigInteger("0", 16);

            for(long isubpart = istartsubpart; isubpart > ifinalsubpart; isubpart--){
                
                long irecsubpart = PartTree.getRecordindex(ORDER, lsubpart, isubpart);


                //key.partIndex = isubpart; key.partLevel = lsubpart;

                PartEntry child = table.get(irecsubpart);

                if(child == null){
                    child = PartTree.newPartEntry(ORDER, LEVELS, isubpart, isubpart, 0, "");//new PartEntry(isubpart, lsubpart, 0);
                    child.digest = PartTree.partDigest(isubpart, lsubpart, 0, child.level == LEVELS);
                    table.put(irecsubpart, child);
                }

                b = PartTree.subpartDigest(b, child.digest);
                //b.add((new BigInteger(child.partDigest, 16)).abs());
            }

            return PartTree.partDigest(entry.level, entry.index, entry.checkpoint, b);

    }

    public void doStamp(long checkpoint, IMemory storage, boolean all)
        throws Exception
    {
        if(all){
            long npages = storage.getCurrentNumberOfPages();
            PageIndexList iTotalPages = new PageIndexList();

            for(long ipage = 0; ipage < npages; ipage++){
                iTotalPages.add(ipage);
            }

            doStamp(iTotalPages, checkpoint, storage);
            return;
        }

        doStamp(storage.getRecentlyModifiedPageIndexes(), checkpoint, storage);
    }
    
    public void doStamp( PageIndexList iPagesUsed, long checkpoint, IMemory storage)
        throws Exception
    {
        Collections.sort(iPagesUsed);

        doStampPageParts(iPagesUsed, checkpoint, storage);
        doStampMetaParts(iPagesUsed, checkpoint, storage);

    }
    
    public String digest(PartEntry entry, boolean isPagePart) throws Exception{
        if(isPagePart)
            return leafDigest(entry, (IPage)null);

        return partDigest(entry, (BigInteger)null);
    }

    public static String partDigest(long ipart, long lpart, long cpart, boolean isPagePart) throws Exception{
        if(isPagePart){
            return leafPartDigest(cpart, cpart, (IPage)null);
        }

        return partDigest(cpart, cpart, cpart, (BigInteger)null);
    }
    public String partDigest(PartEntry entry, BigInteger sum) throws Exception{
        long lpart = entry.level, ipart = entry.index, cpart = entry.checkpoint;
        return partDigest(lpart, ipart, cpart, sum);
    }

    public static BigInteger subpartDigest(BigInteger sum, String cSubpartDigest){
        sum.add((new BigInteger(cSubpartDigest, 16)).abs());
        return sum;
    }
    
    public static String partDigest(
            long partlevel,
            long partindex,
            long partcheckpoint,
            BigInteger sum
     ) throws Exception{
        String s = partlevel + "" + partindex  + "" + partcheckpoint;
        if(sum != null){
            s += sum.toString(16);
        }
        return XSecurity.getDigest(s);
    }

    public String leafDigest(PartEntry entry, IPage page) throws Exception{
        long ipart = entry.index;
        long cpart = entry.checkpoint;

        return PartTree.leafPartDigest(ipart, cpart, page);
    }

    public static String leafPartDigest(long partindex, long partcheckpoint, IPage page) throws Exception{
        
        byte[] data = null;

        if(page != null){
            data = page.getBytes();

        }
        return leafPartdigest(partindex, partcheckpoint, data);
        
    }
    public static String leafPartdigest(long partindex, long partcheckpoint, byte[] pagedata ) throws Exception{
        String digest = "";
        digest += partindex + partcheckpoint;

        if(pagedata != null){
            digest += new String(pagedata, 0, (int)pagedata.length);
        }
        return XSecurity.getDigest(digest);
        
    }

}
