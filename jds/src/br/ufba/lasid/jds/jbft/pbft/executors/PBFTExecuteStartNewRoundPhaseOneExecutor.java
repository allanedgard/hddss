/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferReceivedRequestAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteStartNewRoundPhaseTwoAction;
import br.ufba.lasid.jds.jbft.pbft.actions.RetransmiteReplyAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTExecuteStartNewRoundPhaseOneExecutor extends PBFTServerExecutor{

    public PBFTExecuteStartNewRoundPhaseOneExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage m = (PBFTMessage) act.getWrapper();

        if(!isServerAuthenticated(m)){
            System.out.println(getDefaultSecurityExceptionMessage(m, "start new round phase one"));
            return;            
        }

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "is going to start a new round of the protocol for request  "
          + m.getContent() + " at time " + ((PBFT)getProtocol()).getTimestamp()
        );

        /* buffer the received request */
        getProtocol().perform(new BufferReceivedRequestAction(m));

        /* perform the retransmission if there is a related replay */
        getProtocol().perform(new RetransmiteReplyAction(m));

        /* perform the phase two */
        getProtocol().perform(new ExecuteStartNewRoundPhaseTwoAction(m));

        
    }




}
