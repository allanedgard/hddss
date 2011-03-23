/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.state.managers;

import br.ufba.lasid.jds.ISystemEntity;
import br.ufba.lasid.jds.comm.communicators.ICommunicator;

/**
 *
 * @author aliriosa
 */
public interface IDistributedStateManager extends IStateManager{

    public void retrieve(
        long          pageindex,
        ISystemEntity entityfrom,
        ISystemEntity entityto,
        ICommunicator communicator
    ) throws Exception;
    
    public void transfer(
        long          pageindex,
        ISystemEntity entityfrom,
        ISystemEntity entityto,
        ICommunicator communicator
    ) throws Exception;


}
