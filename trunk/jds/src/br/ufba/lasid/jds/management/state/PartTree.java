/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.pages.PageIndexList;
import br.ufba.lasid.jds.security.util.XSecurity;
import br.ufba.lasid.jds.util.Debugger;
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
    
    public PartTree(String checkpointStoreID, int cacheSize, int LEVELS, int ORDER) throws Exception{
        this.LEVELS = LEVELS;
        this.ORDER = ORDER;
        table = new PartTable(checkpointStoreID, cacheSize);
    }

    public PartTable getPartTable(){
        return this.table;
    }

    public class PartKey implements Serializable{
        long index = 0;
        long level = 0;

        public PartKey(long level, long index){
            this.index = index;
            this.level = level;
        }
        
        public long getRecordIndex(){

            long recindex = 1;

            for(int L = 0; L <= this.level - 1; L++){
                recindex += (long)Math.pow(ORDER, L);
            }

            return recindex + this.index;

        }

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
    }

    public class PartTable  implements Serializable{
        private static final long serialVersionUID = -7049378239504881330L;

        RecordManager recman;
        BTree btree;
        String checkpointStorageID;
        String btreename = "__checkpoint__btree";
        long recid;
        
        public PartTable(String checkpointStorageID, int cacheSize) throws Exception{
            this.checkpointStorageID = checkpointStorageID;
            btreename += checkpointStorageID;
            recman = new CacheRecordManager(RecordManagerFactory.createRecordManager(checkpointStorageID), new MRU(cacheSize));
        }

        public void open() throws Exception{
            recid = recman.getNamedObject(btreename);

            if(recid    != 0){
                btree = BTree.load(recman, recid);
                Debugger.debug(
                    "[PartTable] Reloading existing B+Tree (" + checkpointStorageID + ") " +
                    "with " + btree.size() + " records"
                 );
            }else{

                Comparator<Long> comparator = new Comparator<Long>() {
                    public int compare(Long o1, Long o2) {
                        return o1.compareTo(o2);
                    }
                };

                btree = BTree.createInstance(recman, comparator);
                recman.setNamedObject(btreename, btree.getRecid());

                Debugger.debug(
                    "[PartTable] Created a B+Tree (" + checkpointStorageID + ") " +
                    "with " + btree.size() + " records"
                 );
            }
            
        }
        public synchronized PartEntry get(PartKey key) throws Exception{
            PartEntry entry = null;
            if(key != null){
                entry = (PartEntry)btree.find(key.getRecordIndex());
                if(entry.index == key.index && entry.level == key.level){
                    return entry;
                }
            }

            return entry;
        }

        public synchronized  void commit() throws Exception{
            recman.commit();
        }
        
        public synchronized void put(PartKey key, PartEntry value) throws Exception{
            if(value != null && key != null){
                btree.insert(key.getRecordIndex(), value, true);
            }
        }

        public synchronized void remove(PartKey key) throws Exception{
            if(key != null){
                btree.remove(key.getRecordIndex());
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
    
    protected void stampPageParts(PageIndexList iPagesUsed, long checkpoint, IMemory storage)
            throws Exception
    {
        PartKey key = new PartKey(0, 0);
        
        for(Long ipage : iPagesUsed){
            
            long ipart = ipage;
            
            key.index = ipart; key.level = LEVELS;

            PartEntry entry = table.get(key);

            if(entry == null){
                entry = new PartEntry(ipart, LEVELS);
                table.put(key, entry);                
            }

            entry.checkpoint = checkpoint;
            entry.digest = digest(entry, storage.readPage(ipart));

        }        
    }
    protected void stampMetaParts(PageIndexList iPagesUsed, long checkpoint, IMemory storage)
            throws Exception
    {
        PartKey key = new PartKey(0, 0);

        for(Long ipage : iPagesUsed){

            long whocare = ipage + 1;

            for(int level = LEVELS-1; level > 0; level--){

                long nparts = (long) Math.pow(ORDER, LEVELS - level); 

                whocare = (long)Math.ceil((ipage + 1)/nparts);

                long ipart = whocare - 1;

                key.level = level; key.index = ipart;

                PartEntry entry = table.get(key);

                if(entry == null){
                    entry = new PartEntry(ipart, level);
                    table.put(key, entry);
                }

                entry.checkpoint = checkpoint;

                long ifinal    = whocare * ORDER;
                long istart    = ifinal - ORDER;

                long isublevel = level + 1;

                BigInteger b = new BigInteger("0", 16);

                for(long isubpart = ifinal-1; isubpart > istart; isubpart--){

                    key.index = isubpart; key.level = isublevel;
                    
                    PartEntry child = table.get(key);

                    if(child == null){
                        child = new PartEntry(isubpart, isublevel, 0);
                        child.digest = digest(child, child.level == LEVELS);
                        table.put(key, child);
                    }

                    b.add((new BigInteger(child.digest, 16)).abs());
                }

                entry.digest = digest(entry, b);

            }
        }
    }

    public void stamp(long checkpoint, IMemory storage, boolean all)
        throws Exception
    {
        if(all){
            long npages = storage.getCurrentNumberOfPages();
            PageIndexList iTotalPages = new PageIndexList();

            for(long ipage = 0; ipage < npages; ipage++){
                iTotalPages.add(ipage);
            }

            stamp(iTotalPages, checkpoint, storage);
        }

        stamp(storage.getRecentlyModifiedPageIndexes(), checkpoint, storage);
    }
    
    public void stamp( PageIndexList iPagesUsed, long checkpoint, IMemory storage)
        throws Exception
    {
        Collections.sort(iPagesUsed);

        stampPageParts(iPagesUsed, checkpoint, storage);
        stampMetaParts(iPagesUsed, checkpoint, storage);

    }
    
    public String digest(PartEntry entry, boolean isPagePart) throws Exception{
        if(isPagePart)
            return digest(entry, (IPage)null);

        return digest(entry, (BigInteger)null);
    }
    
    public String digest(PartEntry entry, BigInteger sum) throws Exception{
        String s = entry.level + "" + entry.index  + "" + entry.checkpoint;
        if(sum != null){
            s += sum.toString(16);
        }
        return XSecurity.getDigest(s);
    }

    public String digest(PartEntry entry, IPage page) throws Exception{
        String digest = "";
        digest += entry.index + entry.checkpoint;
        
        if(page != null){
            byte[] data = page.getBytes();
            digest += new String(data, 0, (int)page.getSize());
        }
        return XSecurity.getDigest(digest);
    }

}
