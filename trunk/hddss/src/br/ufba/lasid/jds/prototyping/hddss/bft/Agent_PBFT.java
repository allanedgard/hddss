/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.bft;

import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.prototyping.hddss.cs.Agent_ServiceComponent;
import br.ufba.lasid.jds.prototyping.hddss.cs.comm.SimulatedClientServerCommunicator;

/**
 *
 * @author aliriosa
 */
public class Agent_PBFT extends Agent_ServiceComponent{

    @Override
    public void setup() {
        super.setup();
        proto = new PBFT();
        proto.setCommunicator(new SimulatedClientServerCommunicator(this));
        proto.setLocalProcess(this);

    }

    @Override
    public void multicast(Message m, Process group) {
        getProtocol().getCommunicator().multicast(m, group);
    }

}
