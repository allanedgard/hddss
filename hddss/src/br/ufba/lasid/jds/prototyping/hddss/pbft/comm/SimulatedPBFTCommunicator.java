/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft.comm;

import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.prototyping.hddss.cs.comm.SimulatedClientServerCommunicator;

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
        Process<Integer> dest = (Process<Integer>) group;

        int source = ((Process<Integer>)agent).getID().intValue();
        int destin = dest.getID().intValue();
        int now   = (int) agent.infra.clock.value();

        agent.send(
         new br.ufba.lasid.jds.prototyping.hddss.Message(
            source, destin, m.getType(), 0, now, m
         )
        );
        
    }



}
