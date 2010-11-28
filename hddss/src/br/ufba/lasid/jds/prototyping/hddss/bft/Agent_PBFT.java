/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.bft;

import br.ufba.lasid.jds.prototyping.hddss.cs.comm.SimulatedClientServerCommunicator;
import br.ufba.lasid.jds.comm.Communicator;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.prototyping.hddss.Agent;

/**
 *
 * @author aliriosa
 */
public class Agent_PBFT extends Agent implements Communicator, Process<Integer>{

    PBFT pbft = new PBFT();
    Communicator comm;
    double prob = 0.0;

    public void setPacketGenerationProb (String po) {
        prob = Double.parseDouble(po);
    }

    @Override
    public void setup() {
        super.setup();
        
        pbft.setLocalProcess(this);
        pbft.setCommunicator((Communicator)(new SimulatedClientServerCommunicator(this)));
        
    }

    @Override
    public void receive(br.ufba.lasid.jds.prototyping.hddss.Message msg) {
        PBFTMessage m = (PBFTMessage) msg.getContent();
        pbft.doAction(m);

    }

    public void multicast(Message m, Process group) {
        pbft.getCommunicator().multicast(m, group);
    }

    public void unicast(Message m, Process process) {
        pbft.getCommunicator().unicast(m, process);
    }

    public Integer getID() {
        return new Integer(id);
    }

    public void setID(Integer id) {
        this.id = id.intValue();
    }

}
