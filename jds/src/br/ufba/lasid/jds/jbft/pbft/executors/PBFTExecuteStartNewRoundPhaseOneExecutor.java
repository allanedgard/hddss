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

        /**
         * If the replica is locked then it can buffer requests and response for
         * replays that have been already served. However, it can't proceed with
         * the normal phases of the protocol (this is, doing the pre-prepare, 
         * prepare, commit phases). Therefore, checkpoint, view-change and
         * new-view messages are allowed.
         *
         * When the protocol is unlocking, it has to execute the second phase of
         * the start new round for each request that was buffered while the
         * protocol had been locked.
         *
         * Alirio Sá (2010.12.16)
         */

        
        /* buffer the received request */
        getProtocol().perform(new BufferReceivedRequestAction(m));

        /* perform the retransmission if there is a related replay */
        getProtocol().perform(new RetransmiteReplyAction(m));

        if(((PBFT)getProtocol()).isUnlooked()){

            /* perform the phase two */
            getProtocol().perform(new ExecuteStartNewRoundPhaseTwoAction(m));

            return;

        }

    }

}
