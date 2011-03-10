/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft.comm;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.comm.communicators.PBFTCommunicator;
import br.ufba.lasid.jds.prototyping.hddss.Agent;

/**
 *
 * @author aliriosa
 */
public class SimulatedPBFTCommunicator extends PBFTCommunicator{
    
    protected Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
    
    public SimulatedPBFTCommunicator(Agent agent) {
         setAgent(agent);
    }

    @Override
    public void multicast(IMessage m, IGroup g) {
        
        synchronized(agent.lock){
            int source = agent.ID;
            int now   = (int)agent.infra.clock.value();
            
            for(Object p : g.getMembers()){
                int dest = (Integer) ((IProcess)p).getID();
                int destin = dest;

                agent.send(
                 new br.ufba.lasid.jds.prototyping.hddss.Message(
                    source, destin, 0, 0, now, m
                 )
                );
                
                agent.lock.notify();
            }
        }
    }

    public void unicast(IMessage m, IProcess p) {
        synchronized(agent.lock){
            int dest = (Integer) p.getID();

            int source = agent.ID;
            int destin = dest;
            int now   = (int) agent.infra.clock.value();
            int type  = 0;

            agent.send(
             new br.ufba.lasid.jds.prototyping.hddss.Message(
                source, destin, type, 0, now, m
             )
            );
            agent.lock.notify();
        }
    }

    @Override
    public void receive(IMessage m) {
        synchronized(agent.lock){
            //Debugger.debug("[p"+agent.ID +"] received " + m + " at time " +agent.infra.clock.value());
            super.receive(m);
            agent.lock.notify();
        }
    }

}
