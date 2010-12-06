/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.actions.SendReplyAction;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTRetransmiteReplayExecutor extends PBFTServerExecutor{

    public PBFTRetransmiteReplayExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage request = (PBFTMessage) act.getWrapper();
        Buffer buffer = ((PBFT)getProtocol()).getRequestBuffer();
        
        if(PBFT.hasBeenAlreadyServed(buffer, request)){
            
            if(!isServerAuthenticated(request)){
                System.out.println(getDefaultSecurityExceptionMessage(request, "retransmite replay to"));
                return;
            }

            getProtocol().perform(new SendReplyAction(request));
            return;
        }

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
            + "ignored re-reply for" + request.getContent() + ". It can be a "
            + "duplicated \n\t or request which hasn't been commited yet."
        );

    }


}
