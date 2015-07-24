/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state.decorators;

import br.ufba.lasid.jds.util.JDSUtility;
import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.IVolatileMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.pages.PageIndexList;
import br.ufba.lasid.jds.management.trash.state.BaseStateManager;
import br.ufba.lasid.jds.management.trash.state.FreeStateChunkTable;
import br.ufba.lasid.jds.management.trash.state.IPersistentStateManager;
import br.ufba.lasid.jds.management.trash.state.IState;
import br.ufba.lasid.jds.ft.util.PartTree;
import br.ufba.lasid.jds.management.trash.state.VariableTable;
import br.ufba.lasid.jds.security.util.XSecurity;
import br.ufba.lasid.jds.util.XObject;
import java.util.Properties;

/**
 * Recoverable State with Persitent Memory
 * @author aliriosa
 */
public class RPStateManager extends BaseStateManager implements IPersistentStateManager{

    String storageID = "";
    Properties options = new Properties();
    long checkpointID = 0;
    
    public RPStateManager(Properties options){
        if(options != null){
            this.options.putAll(options);
        }
    }
    
    private String getFreeSpaceStorageID() throws Exception{
        String fssid = getFullStorageID();
        fssid += ".free";
        return fssid;
    }

    private String getCheckpontStorageID(String _storageID) throws Exception{
        return _storageID + ".chpt";
    }
    
    private String getVariableStorageID() throws Exception{
        return getFullStorageID() + ".vars";
    }

    private String getDataStorageID() throws Exception{
        
            return getFullStorageID() + ".data";
        
    }
    
    public void setStorageID(String storageID) throws Exception {
        this.storageID = storageID;
    }

    public String getStorageID() throws Exception {
        return this.storageID;
    }

    public String getFullStorageID() throws Exception{
        return this.checkpointID +"@"+this.storageID;
    }

    public void checkpointMeta(long checkpointID) throws Exception {
      /*#####################################################
       * Initiate variables.
       ######################################################*/
        byte[] bytes;
        
        int maxCacheSize = (int)getMaximumCacheSize();
        int btreeOrder = getBTreeStructureOrder();
        int btreeDepth = getBTreeStructureMaximumDepth();
        this.checkpointID = checkpointID;
      /*#####################################################
       * Download Meta storages
       ######################################################*/
        downloadMeta();
        
      /*#####################################################
       * Instantiate storages
       ######################################################*/
        IMemory fStorage = getStorage( getFreeSpaceStorageID() );
        IMemory vStorage = getStorage( getVariableStorageID()  );

        String cfStorageID = getCheckpontStorageID( getFreeSpaceStorageID() );
        String cvStorageID = getCheckpontStorageID( getVariableStorageID() );

      /*#####################################################
       * Checkpoint state variables descriptors
       ######################################################*/
        PartTree vptree = new PartTree(cvStorageID, maxCacheSize, btreeDepth, btreeOrder);
        vptree.getPartTable().open();
        vptree.doStamp(checkpointID, vStorage, true);
        vptree.getPartTable().commit();
        vptree.getPartTable().close();
        vStorage.release();
        
      /*#####################################################
       * Checkpoint the pointers for free space.
       ######################################################*/
        PartTree fptree = new PartTree(cfStorageID, maxCacheSize, btreeDepth, btreeOrder);
        fptree.getPartTable().open();
        fptree.doStamp(checkpointID, fStorage, true);
        fptree.getPartTable().commit();
        fptree.getPartTable().close();
        fStorage.release();

    }

    public void checkpointData(long checkpointID, boolean execDownloadData) throws Exception {

      /*#####################################################
       * Initiate variables.
       ######################################################*/
        IState currentState = getState();
        String cdStorageID = getCheckpontStorageID(getDataStorageID());
        int maxCacheSize = (int)getMaximumCacheSize();
        int btreeOrder = getBTreeStructureOrder();
        int btreeDepth = getBTreeStructureMaximumDepth();
        this.checkpointID = checkpointID;


      /*#####################################################
       * Checkpoint the state data.
       ######################################################*/
        if(execDownloadData){
            downloadData();
        }
        
        PartTree dptree = new PartTree(cdStorageID, maxCacheSize, btreeDepth, btreeOrder);
        dptree.getPartTable().open();
        dptree.doStamp(checkpointID, currentState.getMemory(), execDownloadData);
        dptree.getPartTable().commit();
        dptree.getPartTable().close();


    }

    public void checkpoint(long checkpointID, boolean execDownloadData) throws Exception {
        this.checkpointID = checkpointID;
        checkpointMeta(checkpointID);
        checkpointData(checkpointID, execDownloadData);
    }

    public void checkpoint(long checkpointID) throws Exception {
        this.checkpointID = checkpointID;
        checkpoint(checkpointID, hasAVolatileStateData());
    }

    public void rollbackMeta(long checkpointID) throws Exception {
      /*#####################################################
       * Initiate variables.
       ######################################################*/
        this.checkpointID = checkpointID;

        fillupMeta();

    }
    public void rollback(long checkpointID, boolean execRollbackData) throws Exception {
        this.checkpointID = checkpointID;
        rollbackMeta(checkpointID);
        rollbackData(checkpointID, execRollbackData);
    }


    public void rollbackData(long checkpointID, boolean execRollbackData) throws Exception {
        this.checkpointID = checkpointID;
      /*#####################################################
       * Checkpoint the state data.
       ######################################################*/
        if(execRollbackData){
            fillupData();
        }
    }

    public void rollback(long checkpointID) throws Exception {
        this.checkpointID = checkpointID;
        rollback(checkpointID, hasAVolatileStateData());
    }
    
    public void fillup(boolean hasVolatileStateData) throws Exception {
        fillupMeta();

        if(hasVolatileStateData){
            fillupData();
        }
    }

    public void fillupData() throws Exception {
      /*#####################################################
       * Initiate variables.
       ######################################################*/
        IState currentState = getState();
        long npages = currentState.getCurrentNumberOfPages();

      /*#####################################################
       * Instantiate storages
       ######################################################*/
        IMemory dStorage = getStorage( getDataStorageID()      );

      /*#####################################################
       * FillUp the state data.
       ######################################################*/
        for(long index = 0; index < npages; index ++){
            IPage page = dStorage.readPage(index);
            currentState.writePage(page);
        }
    }

    public void fillupMeta() throws Exception {
      /*#####################################################
       * Initiate variables.
       ######################################################*/
        byte[] bytes;
        long nbytes = 0;
        
      /*#####################################################
       * Instantiate storages
       ######################################################*/
        IMemory fStorage = getStorage( getFreeSpaceStorageID() );
        IMemory vStorage = getStorage( getVariableStorageID()  );


      /*#####################################################
       * FillUp state variables descriptors
       ######################################################*/
        nbytes = vStorage.getCurrentAllocatedSize();
        bytes = new byte[(int)nbytes];
        vStorage.read(bytes, 0, (int)nbytes);
        VariableTable vars = (VariableTable) XObject.byteArrayToObject(bytes);
        setVariableTable(vars);
        vStorage.release();

      /*#####################################################
       * FillUp the pointers for free space.
       ######################################################*/
        nbytes = fStorage.getCurrentAllocatedSize();
        bytes = new byte[(int)nbytes];
        fStorage.read(bytes, 0, (int)nbytes);
        FreeStateChunkTable fchucks = (FreeStateChunkTable) XObject.byteArrayToObject(bytes);
        setFreeSpaceTable(fchucks);
        fStorage.release();
    }

    public void fillup() throws Exception {
        fillup(hasAVolatileStateData());
    }

    private IMemory getStorage(String _storageID) throws Exception{
        Properties _options = new Properties(options);
        _options.putAll(this.options);
        _options.put(JDSUtility.PersistentStorageID, _storageID);
        IMemory _memory = JDSUtility.create(JDSUtility.PersistentMemoryProvider, _options);
        return _memory;
    }
    
    @Override
    public void downloadMeta() throws Exception{
      /*#####################################################
       * Initiate variables.
       ######################################################*/
        byte[] bytes;

      /*#####################################################
       * Instantiate storages
       ######################################################*/
        IMemory fStorage = getStorage( getFreeSpaceStorageID() );
        IMemory vStorage = getStorage( getVariableStorageID()  );

      /*#####################################################
       * Download state variables descriptors
       ######################################################*/
        VariableTable vars = getVariableTable();
        bytes = XObject.objectToByteArray(vars);
        vStorage.seek(0);
        vStorage.write( bytes );
        vStorage.release();

      /*#####################################################
       * Download the pointers for free space.
       ######################################################*/
        FreeStateChunkTable fchucks = getFreeSpaceTable();
        bytes = XObject.objectToByteArray(fchucks);
        fStorage.write(bytes);
        fStorage.release();
        
    }
    
    @Override
    public void downloadData() throws Exception{
      /*#####################################################
       * Initiate variables.
       ######################################################*/
        byte[] bytes;
        IState currentState = getState();
        PageIndexList indexes = currentState.getRecentlyModifiedPageIndexes();

      /*#####################################################
       * Instantiate storages
       ######################################################*/
        IMemory dStorage = getStorage( getDataStorageID()      );

      /*#####################################################
       * Download the state data.
       ######################################################*/
        for(Long index : indexes){
            IPage page = currentState.readPage(index);
            dStorage.writePage(page);
        }

        dStorage.release();
        
    }
    
    @Override
    public void download(boolean hasVolatileStateData) throws Exception{
        
        downloadMeta();

        if(hasVolatileStateData){
            downloadData();
        }
        
    }
    
    public void download() throws Exception {
        
        download(hasAVolatileStateData());
    }

    public String getStamp(byte[] bytes, int offset, int length) throws Exception{
        //do nothing
        return "";
    }

    public String getStamp(String lastStamp, byte[] bytes, int offset, int length) throws Exception{
        //do nothing
        return "";
    }

    public boolean checkStamp(String inStamp, byte[] bytes, int offset, int length) throws Exception{
        //do nothing
        return true;        
    }

    public String getStamp(IPage page) throws Exception {
        return "";
    }

    public String getStamp(String checkpointID, IPage page) throws Exception {
        long psize = page.getSize();
        byte[] bytes = page.getBytes();

        String s = "" + page.getIndex() + "" + checkpointID + new String(bytes, 0, (int)psize);

        String digest = XSecurity.getDigest(s);
        
        return digest;
    }

    public boolean hasAVolatileStateData() throws Exception{

        return getState().getMemory() instanceof IVolatileMemory;
//        String volatiteStateData = options.getProperty( JDSUtility.hasAVolatileStateMemory,
//                                                        "true");
//
//        return Boolean.parseBoolean(volatiteStateData);

    }

    public long getMaximumCacheSize(){
        String mcs = options.getProperty( JDSUtility.MaximumCacheSize,
                                          "256" );

        return Long.parseLong(mcs);
        
    }

    public int getBTreeStructureMaximumDepth(){
        String btsmd = options.getProperty( JDSUtility.BTreeStructureMaximumDepth,
                                            "4" );

        return Integer.parseInt(btsmd);
    }

    public int getBTreeStructureOrder(){
        String btsmd = options.getProperty( JDSUtility.BTreeStructureOrder,
                                            "256" );
        
        return Integer.parseInt(btsmd);        
    }

}
