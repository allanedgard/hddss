/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.state.managers;

import br.ufba.lasid.jds.ft.util.CheckpointLog;
import br.ufba.lasid.jds.ft.util.CheckpointLogEntry;
import br.ufba.lasid.jds.ft.util.PartList;
import br.ufba.lasid.jds.ft.util.PartTree.PartEntry;
import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.IPersistentMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.state.IState;
import br.ufba.lasid.jds.management.memory.storages.IStorage;
import br.ufba.lasid.jds.ft.util.PartTree;
import br.ufba.lasid.jds.util.JDSUtility;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author aliriosa
 */
public class RecoverableStateManager implements IRecovarableStateManager{
    IStateManager base;
    long checkpointID = -1;
    String validationString = "";

    String objectStorageID = "";

    CheckpointLog clog = new CheckpointLog();

    public boolean existsLogEntry(long checkpointID){
        return clog.containsKey(checkpointID);
    }
    
    public void addLogEntry(CheckpointLogEntry clogEntry){

        if(clogEntry != null){
            clog.put(clogEntry.getCheckpointID(), clogEntry);
        }
        
    }

    public CheckpointLogEntry getLogEntry(long checkpointID){
        return clog.get(checkpointID);
    }

    public void removeLogEntry(long checkpointID){
        clog.remove(checkpointID);
    }

    public CheckpointLogEntry getBiggestLogEntry(){
        return clog.getBiggest();
    }

    public PartTree.PartEntry getPart(long lpart, long ipart) throws Exception{

        PartTree.PartEntry entry = null;
        /**
         * Gets checkpoint storage id for the volatile data
         */
        String csid = GETCHECKPOINTSTORAGEID(getObjecStorageID());;

        /**
         * Gets snapshot storage id for the volatile data
         */
        PartTree ptree = GETINSTANCEOFPARTTREE(csid);

        ptree.getPartTable().open();
        
        entry = ptree.getPartTable().getPart(lpart, ipart);

        ptree.getPartTable().close();

        return entry;
        
    }

    public PartList getFamily(long lpart, long ipart, long minage) throws Exception{
        
        PartList members = new PartList();

        /**
         * Gets checkpoint storage id for the volatile data
         */
        String csid = GETCHECKPOINTSTORAGEID(getObjecStorageID());;

        /**
         * Gets snapshot storage id for the volatile data
         */
        PartTree ptree = GETINSTANCEOFPARTTREE(csid);

        ptree.getPartTable().open();

        members = ptree.getPartTable().getFamily(lpart, ipart, minage);

        ptree.getPartTable().close();

        return members;
    }

    public PartList getSubparts(long lpart, long ipart) throws Exception{
        PartList subparts = new PartList();
        /**
         * Gets checkpoint storage id for the volatile data
         */
        String csid = GETCHECKPOINTSTORAGEID(getObjecStorageID());;

        /**
         * Gets snapshot storage id for the volatile data
         */
        PartTree ptree = GETINSTANCEOFPARTTREE(csid);

        ptree.getPartTable().open();

        PartList _subparts = ptree.getPartTable().getSubparts(lpart, ipart);

        subparts.addAll(_subparts);
        
        ptree.getPartTable().close();
        
        return subparts;
    }

//    public PartList getPartsGreaterOrEqualThen(long checkpointID, long level, long ipart) throws Exception{
//        return null;
//    }
//    public PartList getPartsGreaterOrEqualThen(long checkpointID) throws Exception{
//        boolean SELECTBYCHECHECKPOINTID = false;
//
//        /**
//         * Gets checkpoint storage id for the volatile data
//         */
//        String csid = GETCHECKPOINTSTORAGEID(getObjecStorageID());;
//
//        /**
//         * Gets snapshot storage id for the volatile data
//         */
//        PartTree ptree = GETINSTANCEOFPARTTREE(csid);
//
//        ptree.getPartTable().open(SELECTBYCHECHECKPOINTID);
//        PartList parts = ptree.getPartTable().selectByCheckpoint(checkpointID);
//
//        ptree.getPartTable().close();
//
//        return parts;
//        //ptree.getPartTable().f
//    }

    public RecoverableStateManager(IStateManager manager) throws Exception {
        base = manager;
    }

    public IMemory getDataStorage(String dstorageID) throws Exception{
        String dsid = GETSNAPSHOTSTORAGEID  (dstorageID);
        return GETSNAPSHOTSTORAGE(dsid, getOptions());
    }
    
    public IMemory getObjecStorage() throws Exception{
        String dsid = GETSNAPSHOTSTORAGEID  (getObjecStorageID());
        return GETSNAPSHOTSTORAGE(dsid, getOptions());
    }

    public void checkpoint(long checkpointID) throws Exception {

        /**
         * Gets checkpoint storage id for the volatile data
         */
        String csid = GETCHECKPOINTSTORAGEID(getObjecStorageID());;

        /**
         * Gets snapshot storage id for the volatile data
         */
        String dsid = GETSNAPSHOTSTORAGEID  (getObjecStorageID());

        /**
         * Gets the volatile data memory
         */
        IMemory pm = bufferMemory();
        
        /**
         * Snapshots the volatile data
         */
        SNAPSHOT(pm, dsid);

        /**
         * Creates and stores the partition tree for the volatile data
         */
        PARTTREE(pm, csid, checkpointID);

        /**
         *For each storageid in the set of persistent data storage ids
         */
        for(String storageID : getStorateIDSet()){
            /**
             * Gets the storage by id.
             */
            IPersistentMemory dm = getStorage(storageID);

            /**
             * Gets snapshot storage id of the current storage
             */
            dsid = GETSNAPSHOTSTORAGEID  (storageID);

            /**
             * Gets checkpoint storage id of the current storage
             */
            csid = GETCHECKPOINTSTORAGEID(storageID);

            /**
             * Snapshots the data of the current storage.
             */
            SNAPSHOT(dm, dsid);

            /**
             * Creates and stores the partition tree for the current storage
             */
            PARTTREE(dm, csid, checkpointID);
        }

    }

    public void rollback(long checkpointID) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void rollback() throws Exception {
        /**
         * Gets checkpoint storage id for the volatile data
         */
        String csid = GETCHECKPOINTSTORAGEID(getObjecStorageID());;

        /**
         * Gets snapshot storage id for the volatile data
         */
        String dsid = GETSNAPSHOTSTORAGEID  (getObjecStorageID());

        IMemory ss  = GETSNAPSHOTSTORAGE(dsid, base.getOptions());

        long nbytes = ss.getCurrentAllocatedSize();

        if(nbytes > 0){
            
            byte[] buf = new byte[(int)nbytes];

            ss.seek(0);
            
            if(ss.read(buf, 0, buf.length) > 0){
                setCurrentState(buf);
            }
        }

        ss.release();

        PartTree ptree = GETINSTANCEOFPARTTREE(csid);
        ptree.getPartTable().open();
        PartTree.PartEntry entry = ptree.getPartTable().getRootEntry();
        
        if(entry != null){
            setCurrentCheckpointID(entry.getPartCheckpoint());
            setCurrentValidationString(entry.getDigest());
        }        
    }

//    private void _SNAPSHOT(IMemory inp, String storageout) throws Exception{
//
//        IMemory snapshot = GETSNAPSHOTSTORAGE(storageout, JDSUtility.Options);
//
//        long npages = inp.getCurrentNumberOfPages();
//
//        for(long ipage = 0; ipage < npages; ipage ++){
//            IPage page = inp.readPage(ipage);
//            snapshot.writePage(page);
//        }
//
//        snapshot.release();
//
//    }


    public void checkpoint() throws Exception {
        if(getCurrentCheckpointID() < 0){
            //do nothing.
        }
    }


    public void setObjectStorageID(String osid){
         this.objectStorageID = osid;
    }

    public String getObjecStorageID(){
        return this.objectStorageID;
    }

    public long getCurrentCheckpointID() {
        return checkpointID;
    }

    public void setCurrentCheckpointID(long checkpointID) {
        this.checkpointID = checkpointID;
    }

    public String getCurrentValidationString() {
        return validationString;
    }

    public void setCurrentValidationString(String validationString) {
        this.validationString = validationString;
    }

    public void setCurrentState(IState state) throws Exception {
        base.setCurrentState(state);
    }

    public IState getCurrentState() throws Exception {
        return base.getCurrentState();
    }

    public void setCurrentState(byte[] buf) throws Exception {
        base.setCurrentState(buf);
    }

    public IMemory bufferMemory() throws Exception {
        return base.bufferMemory();
    }

    public void set(String variable, Object value) throws Exception {
        base.set(variable, value);
    }

    public Object get(String variable) throws Exception {
        return base.get(variable);
    }

    public IStorage getStorage(String storageID) {
        return base.getStorage(storageID);
    }

    public void connect(String storageID, IStorage storage) {
        base.connect(storageID, storage);
    }

    public void disconnect(String storageID) {
        base.disconnect(storageID);
    }

    public Set<String> getStorateIDSet(){
        return base.getStorateIDSet();
    }


    /**** Utiliy *****/

    private PartTree GETINSTANCEOFPARTTREE(String storageID) throws Exception{
        int  mcs = GETMAXIMUMCACHESIZE();
        int  btd = getParttreeLevels();
        int  bto = getParttreeOrder();

        PartTree ptree = new PartTree(storageID, mcs, btd, bto);
        return ptree;        
    }
    
    private void PARTTREE(IMemory in, String storageID, long checkpointID) throws Exception{
        int  mcs = GETMAXIMUMCACHESIZE();
        int  btd = getParttreeLevels();
        int  bto = getParttreeOrder();

        PartTree ptree = new PartTree(storageID, mcs, btd, bto);
        ptree.getPartTable().open();
        ptree.doStamp(checkpointID, in, true);
        ptree.getPartTable().commit();
        ptree.getPartTable().close();
    }

    private void SNAPSHOT(IMemory inp, String storageout) throws Exception{

        IMemory snapshot = GETSNAPSHOTSTORAGE(storageout, getOptions());

        long npages = inp.getCurrentNumberOfPages();

        for(long ipage = 0; ipage < npages; ipage ++){
            IPage page = inp.readPage(ipage);
            snapshot.writePage(page);
        }

        snapshot.release();

    }

    

    public void snapshot(IMemory inp, String storageout, long ipage) throws Exception{

        IMemory snapshot = GETSNAPSHOTSTORAGE(storageout, getOptions());

        long npages = inp.getCurrentNumberOfPages();

        if(ipage < npages){
            IPage page = inp.readPage(ipage);
            snapshot.writePage(page);
        }

        snapshot.release();

    }
    
    private IMemory GETSNAPSHOTSTORAGE(String _storageID, Properties options) throws Exception{
        Properties _options = new Properties(options);
        _options.put(JDSUtility.PersistentStorageID, _storageID);
        _options.put(JDSUtility.Filename, _storageID);

        IMemory _memory = JDSUtility.create(JDSUtility.PersistentMemoryProvider, _options);
        return _memory;
    }

    private int GETMAXIMUMCACHESIZE(){
        Properties ioptions = new Properties(JDSUtility.Options);
        ioptions.putAll(base.getOptions());
        
        String mcs = ioptions.getProperty( JDSUtility.MaximumCacheSize);

        return Integer.parseInt(mcs);

    }
    public int getParttreeLevels(){
        Properties ioptions = new Properties(JDSUtility.Options);
        ioptions.putAll(base.getOptions());

        String btsmd = ioptions.getProperty( JDSUtility.BTreeStructureMaximumDepth);

        return Integer.parseInt(btsmd);
    }

    public int getParttreeOrder(){
        Properties ioptions = new Properties(JDSUtility.Options);
        ioptions.putAll(base.getOptions());

        String btsmd = ioptions.getProperty( JDSUtility.BTreeStructureOrder);

        return Integer.parseInt(btsmd);
    }

    private String GETSNAPSHOTSTORAGEID(String storageID){
        return storageID + "." + "snapshot";
    }

    private String GETCHECKPOINTSTORAGEID(String storageID){
        return storageID + "." + "checkpoint";
    }

    public void setOptions(Properties options){
        base.getOptions().putAll(options);
    }

    public Properties getOptions(){
        return base.getOptions();
    }

    public byte[] byteArray() throws Exception {
        return base.byteArray();
    }

    public PartEntry put(Long recid, PartEntry value) throws Exception {
        /**
         * Gets checkpoint storage id for the volatile data
         */
        String csid = GETCHECKPOINTSTORAGEID(getObjecStorageID());;

        /**
         * Gets snapshot storage id for the volatile data
         */
        PartTree ptree = GETINSTANCEOFPARTTREE(csid);

        ptree.getPartTable().open();

        value = ptree.getPartTable().put(recid, value);

        ptree.getPartTable().close();

        return value;
    }

}
