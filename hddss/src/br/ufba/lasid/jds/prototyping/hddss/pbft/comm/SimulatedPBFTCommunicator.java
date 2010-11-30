/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft.comm;

import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.prototyping.hddss.cs.comm.SimulatedClientServerCommunicator;
import br.ufba.lasid.jds.prototyping.hddss.pbft.Agent_PBFT;
import br.ufba.lasid.jds.util.Clock;

/**
 *
 * @author aliriosa
 */
public class SimulatedPBFTCommunicator extends SimulatedClientServerCommunicator{
    
    public SimulatedPBFTCommunicator(Agent agent) {
        super(agent);
    }

    @Override
    public void multicast(Message m, Process group) {
        
        Agent_PBFT ag = (Agent_PBFT) agent;

        Process<Integer> dest = (Process<Integer>) group;

        int source = ((Process<Integer>)agent).getID().intValue();
        int destin = dest.getID().intValue();
        
        int now   = (int)((Clock)ag.getProtocol().getContext().get(PBFT.CLOCKSYSTEM)).value();
        
        int type  = ((PBFTMessage.TYPE)m.get(PBFTMessage.TYPEFIELD)).getValue();
        
        agent.send(
         new br.ufba.lasid.jds.prototyping.hddss.Message(
            source, destin, type, 0, now, m
         )
        );
        
    }



}
