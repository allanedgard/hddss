/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

/**
 *
 * @author aliriosa
 */
public interface IPersistentState<StorageID, Long, VariableID, VariableValue>
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

}
