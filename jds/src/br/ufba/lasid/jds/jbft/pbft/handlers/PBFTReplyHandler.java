/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.PBFTClient;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;

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
        
        if(getProtocol().canProceed(reply)){
            
            if(getProtocol().updateState(reply)){
               getProtocol().getApplicationBox().add(reply.getPayload());
            }//end if updateState(reply)

        }//end if wasAcceptedAsValidReply (reply)
    }


}
