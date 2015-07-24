/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.client.PBFTClient;
import br.ufba.lasid.jds.jbft.pbft.client.decision.ReplySubject;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.management.trash.state.IReplicableState;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class PBFTReplyHandler extends PBFTClientMessageHandler{

    public PBFTReplyHandler(PBFTClient protocol) {
        super(protocol);
    }

    public void handle() {
        PBFTReply reply =  (PBFTReply) this.input;
       getProtocol().handle(reply);

//
//        if(getProtocol().canProceed(reply)){
//            ReplySubject rs = (ReplySubject)getProtocol().getDecision(reply);
//            if(rs != null){
//               IPayload result = (IPayload)rs.getInfo(ReplySubject.PAYLOAD);
//               //getProtocol().getApplicationBox().add(result);
//            }//end if getDecision(reply)

//        }//end if wasAcceptedAsValidReply (reply)
    }


}
