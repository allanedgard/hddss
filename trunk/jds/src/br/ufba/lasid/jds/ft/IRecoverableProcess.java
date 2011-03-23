/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.ft;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.management.memory.state.IState;

/**
 *
 * @author aliriosa
 */
public interface IRecoverableProcess<ProcessID> extends IProcess<ProcessID> {

    public void setCurrentState(IState state);
    public IState getCurrentState();


}
