/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.util.Scheduler;
import br.ufba.lasid.jds.util.Task;
import br.ufba.lasid.jds.cs.executors.ClientServerSendRequestExecutor;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.RetransmissionAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Clock;
import br.ufba.lasid.jds.util.Debugger;
import br.ufba.lasid.jds.util.ExecutorCollection;

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
        
        Group g = (Group) m.get(PBFTMessage.DESTINATIONFIELD);

        m = PBFTMessage.translateTo(m, PBFTMessage.TYPE.RECEIVEREQUEST);

        long timestamp = ((Clock)getProtocol().getContext().get(PBFT.CLOCKSYSTEM)).value();

        m.put(PBFTMessage.TIMESTAMPFIELD, timestamp);
        m.put(PBFTMessage.CLIENTFIELD, getProtocol().getLocalProcess().getID());

        Long timeout = (Long)getProtocol().getContext().get(PBFT.CLIENTRETRANSMISSIONTIMEOUT);

        m.put(PBFT.CLIENTRETRANSMISSIONTIMEOUT, timestamp + timeout.longValue());

        Debugger debugger = (Debugger) getProtocol().getContext().get(PBFT.DEBUGGER);
        
        debugger.debug("[PBFTSendRequestExecutor] PBFTSendRequestExecutor.execute sending of (" + m + ") at time " + timestamp);
        
        getProtocol().getCommunicator().multicast(m, g);                        
        
        Scheduler scheduler = (Scheduler)(getProtocol().getContext().get(PBFT.SCHEDULER));

        ExecutorCollection execs = getProtocol().getExecutors().get(RetransmissionAction.class);
        
        for(Executor e : execs){
            scheduler.schedule((Task)e, timeout);
        }
        
    }
    
}

