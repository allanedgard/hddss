/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Debugger;

/**
 *
 * @author aliriosa
 */
public class PBFTClientRetransmissionExecutor extends PBFTSendRequestExecutor{

    public PBFTClientRetransmissionExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        Debugger debugger = (Debugger)getProtocol().getContext().get(PBFT.DEBUGGER);

        PBFTMessage m = (PBFTMessage) act.getMessage();
        Group g = (Group) m.get(PBFTMessage.DESTINATIONFIELD);
        
        
        getProtocol().getCommunicator().multicast(m, g);

        Long time = ((PBFT)getProtocol()).getTimestamp();

        debugger.debug("[PBFTClientRetransmissionExecutor.execute] resending of (" + m + ") at time " + time);

        scheduleRetransmission(m);
    }
}
