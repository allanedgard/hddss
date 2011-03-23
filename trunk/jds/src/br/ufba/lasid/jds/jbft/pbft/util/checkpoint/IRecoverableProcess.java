/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util.checkpoint;

import br.ufba.lasid.jds.IProcess;

/**
 *
 * @author aliriosa
 */
public interface IRecoverableProcess<ProcessID> extends IProcess<ProcessID>{
       public br.ufba.lasid.jds.management.memory.state.IState getCurrentState();
       public void setCurrentState(br.ufba.lasid.jds.management.memory.state.IState state);

}
