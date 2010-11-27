/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.simulated;

import br.ufba.lasid.hddss.Agent;
import br.ufba.lasid.hddss.Agent_ClientPBFT;
import br.ufba.lasid.hddss.RuntimeSupport;
import br.ufba.lasid.jbft.Communicator;
import br.ufba.lasid.jbft.Message;
import br.ufba.lasid.jbft.Process;

/**
 *
 * @author aliriosa
 */
public class SimulatedCommunicator implements Communicator{
    public static String TAG = "SimulatedCommunicator";
    Agent agent;

    public SimulatedCommunicator(Agent agent){
        this.agent = agent;
    }
    
    public void multicast(Message m, Process group) {
        agent.send(
         new br.ufba.lasid.hddss.Message(
            agent.id,
            agent.infra.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value().intValue(),
            m.getType(), 0, (int) agent.infra.clock.value(),
            m
         )
        );
    }

    public void unicast(Message m, Process client) {
        Agent_ClientPBFT a = (Agent_ClientPBFT) client;
        agent.send(
         new br.ufba.lasid.hddss.Message(
            agent.id,
            a.id,
            m.getType(), 0, (int) agent.infra.clock.value(),
            m
         )
        );

    }

}
