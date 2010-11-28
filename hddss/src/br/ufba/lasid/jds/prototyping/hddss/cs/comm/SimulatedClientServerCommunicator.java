/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.cs.comm;

import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.comm.Communicator;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.Process;

/**
 *
 * @author aliriosa
 */
public class SimulatedClientServerCommunicator implements Communicator{
    public static String TAG = "SimulatedCommunicator";
    Agent agent;

    public SimulatedClientServerCommunicator(Agent agent){
        this.agent = agent;
    }
    
    public void multicast(Message m, Process group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unicast(Message m, Process client) {
        Process<Integer> c = (Process<Integer>) client;
        agent.send(
         new br.ufba.lasid.jds.prototyping.hddss.Message(
            agent.id,
            c.getID().intValue(),
            m.getType(), 0, (int) agent.infra.clock.value(),
            m
         )
        );

    }

}
