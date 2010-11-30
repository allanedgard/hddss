/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.group.SingleGroup;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.prototyping.hddss.cs.Agent_ServiceComponent;
import br.ufba.lasid.jds.prototyping.hddss.pbft.comm.SimulatedPBFTCommunicator;

/**
 *
 * @author aliriosa
 */
public class Agent_PBFT extends Agent_ServiceComponent implements Group<Integer>{

    Group group = new SingleGroup();

    public void setServerGroupAddress(String addr){
        this.setGroupID(new Integer(id));
    }


    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }


    @Override
    public void setup() {
        super.setup();
        setProtocol(new PBFT());
        getProtocol().getContext().put(PBFT.LOCALGROUP, getGroup());
        getProtocol().setCommunicator(new SimulatedPBFTCommunicator(this));
        getProtocol().setLocalProcess(this);

    }

    @Override
    public void multicast(Message m, Process group) {
        getProtocol().getCommunicator().multicast(m, group);
    }

    public void setGroupID(Integer id) {
        group.setGroupID(id);
    }

    public Integer getGroupID() {
        return (Integer)group.getGroupID();
    }

}
