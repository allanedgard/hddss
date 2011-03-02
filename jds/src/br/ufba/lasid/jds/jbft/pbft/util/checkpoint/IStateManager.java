/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util.checkpoint;

/**
 *
 * @author aliriosa
 */
public interface IStateManager<CheckpointID> {

    public void doRegister(IRecoverableProcess process);
    
    public void doSnapshot();
    public void doRecovery();

    public void doSnapshot(CheckpointID checkpointID);
    public void doRecovery(CheckpointID checkpointID);

    public IRecoverableProcess getProcess();

    public CheckpointID getCurrentCheckpointID();

}
