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
import br.ufba.lasid.jds.util.Debugger;

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
        

        int source = agent.ID;

        for(Object p : g.getMembers()){
            int dest = (Integer) ((IProcess)p).getID();
            int destin = dest;

            int now   = (int)agent.infra.clock.value();


            agent.send(
             new br.ufba.lasid.jds.prototyping.hddss.Message(
                source, destin, 0, 0, now, m
             )
            );
        }
    }

    public void unicast(IMessage m, IProcess p) {

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
    }

    @Override
    public void receive(IMessage m) {
        Debugger.debug("[p"+agent.ID +"] received " + m + " at time " +agent.infra.clock.value());
        super.receive(m);
    }

}
