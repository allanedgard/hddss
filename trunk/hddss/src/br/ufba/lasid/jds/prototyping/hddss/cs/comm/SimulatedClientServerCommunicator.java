/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.cs.comm;

import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.comm.Communicator;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;

/**
 *
 * @author aliriosa
 */
public class SimulatedClientServerCommunicator implements Communicator{
    
    protected Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    
    public SimulatedClientServerCommunicator(Agent agent){
        this.agent = agent;
    }
    
    public void multicast(Message m, Process group) {

        throw new UnsupportedOperationException("Not supported yet.");

    }

    public void unicast(Message m, Process destination) {

        Process<Integer> dest = (Process<Integer>) destination;

        int source = agent.id;
        int destin = dest.getID().intValue();
        int now   = (int) agent.infra.clock.value();
        int type  = getTypeValue(m);
        
        agent.send(
         new br.ufba.lasid.jds.prototyping.hddss.Message(
            source, destin, type, 0, now, m
         )
        );

    }

    protected int getTypeValue(Message m){
        return ((ClientServerMessage.TYPE)m.get(ClientServerMessage.TYPEFIELD)).getValue();
    }
}
