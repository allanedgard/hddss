/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

/**
 *
 * @author aliriosa
 */
public interface IPersistentState<StorageID, CheckpointID, VariableID, VariableValue>
        extends IState<VariableID, VariableValue>
{

    /**
     * Sets the persistent repository identity
     * @param ID - the identity.
     * @throws Exception - if an error occurs.
     */
    public void setStorageID(StorageID ID) throws Exception;

    /**
     * Gets the persistent repository identity.
     * @return - the identity.
     * @throws Exception - if a error occurs.
     */
    public StorageID getStorageID() throws Exception;

    /**
     * Saves the current state into a storage.
     * @param checkpointID - the checkpoint identity
     * @throws Exception - if an error occurs.
     */
    public void checkpoint(CheckpointID checkpointID) throws Exception;

    /**
     * Load a state from a storage
     * @param checkpointID - the checkpoint identity
     * @throws Exception - if an error occurs.
     */
    public void rollback(CheckpointID checkpointID) throws Exception;

}
