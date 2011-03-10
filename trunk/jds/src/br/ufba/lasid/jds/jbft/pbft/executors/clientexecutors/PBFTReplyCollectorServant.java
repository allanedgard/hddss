/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors.clientexecutors;

import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCollectorServant;

/**
 *
 * @author aliriosa
 */
public class PBFTReplyCollectorServant extends PBFTCollectorServant<PBFTReply>{
    
    public PBFTReplyCollectorServant(){

    }

    public PBFTReplyCollectorServant(PBFT p){
        setProtocol(p);
        setName(this.getClass().getSimpleName() + "[" + p.getLocalProcessID() +"]");
    }

    /**
     * Collect a reply message sent by a replica. It's also responsable by
     * building the reply quorum.
     */
    protected synchronized boolean accept(PBFTReply m){
        return true;//((PBFTClient)getProtocol()).accept(m);
    }


    public boolean canConsume(Object object) {

        if(object instanceof PBFTReply)
            return true;

        if(object instanceof PDU){
            PDU pdu = (PDU) object;
            return canConsume(pdu.getPayload());
        }

        if(object instanceof SignedMessage){
            try{

                SignedMessage m = (SignedMessage) object;

                return canConsume(m.getSignedObject().getObject());

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        return false;
    }

    
}
