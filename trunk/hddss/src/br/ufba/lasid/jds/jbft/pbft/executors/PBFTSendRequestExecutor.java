/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.util.Scheduler;
import br.ufba.lasid.jds.util.Task;
import br.ufba.lasid.jds.cs.executors.ClientServerSendRequestExecutor;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.RetransmissionAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTSendRequestExecutor extends ClientServerSendRequestExecutor{

    public PBFTSendRequestExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage m = (PBFTMessage) act.getMessage();
        
        Group g = (Group) m.getDestination();

        m.setType(PBFTMessage.TYPE.RECEIVEREQUEST);

        getProtocol().getCommunicator().multicast(m, g);

        Long timeout = (Long)getProtocol().getContext().get(PBFT.CLIENTRETRANSMISSIONTIMOUT);
        
        Scheduler scheduler = (Scheduler)getProtocol().getContext().get(PBFT.SCHEDULER);

        scheduler.schedule(
            (Task)getProtocol().getExecutors().get(RetransmissionAction.class),
            timeout
        );
        
    }
    
}
