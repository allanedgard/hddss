/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.PBFTClient;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.util.Debugger;

/**
 *
 * @author aliriosa
 */
public class PBFTReplyCollectorServant extends PBFTCollectorServant<PBFTReply>{
    
    public PBFTReplyCollectorServant(){

    }

    public PBFTReplyCollectorServant(PBFT p){
        setProtocol(p);
    }

    /**
     * Collect a reply message sent by a replica. It's also responsable by
     * building the reply quorum.
     */
    protected boolean accept(PBFTReply m){
        
        PBFTClient pbft = (PBFTClient)getProtocol();
        /**
         * We must check if the view number is bigger or equal than last received.
         * (We're going to implement this later).
         */

        if(pbft.getLocalProcess().getID().equals(m.getClientID())){
            Quorum q = pbft.getQuorum(m);
            if(!(q != null && q.complete())){
                if(pbft.updateState(m)){

                    q = pbft.getQuorum(m);

                    if(q!=null && q.complete()){
                        pbft.revokeSchedule(m.getTimestamp());
                        Debugger.debug(
                          "[PBFTReplyCollectorServant] c"  + pbft.getLocalProcess().getID() +
                          ", at time " + pbft.getClock().value() + ", revoked the timeout "
                        + "for receive of the " + m
                        );

                        pbft.getApplicationBox().add(m.getPayload());
                    }
                }
            }else{
                if(q.complete()){
                    if(pbft.updateState(m)){
                        pbft.revokeSchedule(m.getTimestamp());
                        Debugger.debug(
                          "[PBFTClient] c"  + pbft.getLocalProcess().getID() +
                          ", at time " + pbft.getClock().value() + ", has just" +
                          " updated the quorum for " + m + "because such " +
                          "quorum has already completed."
                        );
                    }

                }
                
            }
        }

        return false;
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
