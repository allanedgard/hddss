/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

/**
 *
 * @author aliriosa
 */
public interface IRecoverableState {
    /**
     * Saves the current state into a storage.
     * @param checkpointID - the checkpoint identity
     * @throws Exception - if an error occurs.
     */
    public void checkpoint(Long checkpointID) throws Exception;

    /**
     * Load a state from a storage
     * @param checkpointID - the checkpoint identity
     * @throws Exception - if an error occurs.
     */
    public void rollback(Long checkpointID) throws Exception;

}
