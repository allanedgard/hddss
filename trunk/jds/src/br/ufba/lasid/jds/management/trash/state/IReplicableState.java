/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

import br.ufba.lasid.jds.ISystemEntity;
import br.ufba.lasid.jds.comm.communicators.ICommunicator;

/**
 *
 * @author aliriosa
 */
public interface IReplicableState<VariableID, VariableValue> extends IState<VariableID, VariableValue>{

    /**
     * Transfers an page idenfitied by the pageindex to another system entity, 
     * using a communicator. A system entity can be a process or a group of
     * process. If the system entity is a process then the page will be unicast
     * to such system entity, otherwise it will be multicast.
     * @param pageindex - the page index.
     * @param entity - the another system entity.
     * @param communicator - the communicator.
     * @throws Exception - if an error occurs.
     */
    public void transfer(long pageindex, ISystemEntity entity, ICommunicator communicator) throws Exception;

    /**
     * Retrieves an page identified by a pageindex from a another system entity,
     * using a communicator. A system entity can be a process or a group of
     * process. If the system entity is a process then the page will be unicast
     * to such system entity, otherwise it will be multicast.
     * @param pageindex - the page index.
     * @param entity - the another system entity.
     * @param communicator - the communicator.
     * @throws Exception - if an error occurs.
     */
    public void retrieve(long pageindex, ISystemEntity entity, ICommunicator communicator) throws Exception;

}
