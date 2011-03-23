/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

import br.ufba.lasid.jds.management.memory.pages.IPage;

/**
 *
 * @author aliriosa
 */
public interface IPersistentStateManager extends IStateManager{

    /**
     * Sets the persistent repository identity
     * @param ID - the identity.
     * @throws Exception - if an error occurs.
     */
    public void setStorageID(String storageID) throws Exception;

    /**
     * Gets the persistent repository identity.
     * @return - the identity.
     * @throws Exception - if a error occurs.
     */
    public String getStorageID() throws Exception;

    /**
     * Saves the current state into a storage.
     * @param checkpointID - the checkpoint identity
     * @throws Exception - if an error occurs.
     */
    public void checkpoint(long checkpointID) throws Exception;
    public void checkpointMeta(long checkpointID) throws Exception;
    public void checkpoint(long checkpointID, boolean hasAVolatileStateData) throws Exception ;
    public void checkpointData(long checkpointID, boolean execDownloadData) throws Exception;
    /**
     * Load a state from a storage
     * @param checkpointID - the checkpoint identity
     * @throws Exception - if an error occurs.
     */
    public void rollback(long checkpointID) throws Exception;
    public void rollbackMeta(long checkpointID) throws Exception;
    public void rollback(long checkpointID, boolean hasAVolatileStateData) throws Exception ;
    public void rollbackData(long checkpointID, boolean execDownloadData) throws Exception;


    public void fillup() throws Exception;
    public void fillup(boolean hasVolatileStateData) throws Exception;
    public void fillupMeta() throws Exception;
    public void fillupData() throws Exception;

    public void download() throws Exception;
    public void downloadMeta() throws Exception;
    public void downloadData() throws Exception;
    public void download(boolean hasVolatileStateData) throws Exception;


    public String getStamp(byte[] bytes, int offset, int length) throws Exception;
    public String getStamp(String lastStamp, byte[] bytes, int offset, int length) throws Exception;

    public String getStamp(String checkpointID, IPage page) throws Exception;
    //public String getStamp(String lastStamp, IPage page) throws Exception;

    public boolean checkStamp(String inStamp, byte[] bytes, int offset, int length) throws Exception;

}
