/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferChangeViewAckAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteChangeViewRoundThreeAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveChangeViewAckExecutor extends PBFTServerExecutor{

    public PBFTReceiveChangeViewAckExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage ack = (PBFTMessage) act.getWrapper();
        
        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "has received the <CHANGE-VIEW-ACK,  view = "
          + ack.get(PBFTMessage.VIEWFIELD) + ", digest = "
          + ack.get(PBFTMessage.DIGESTFIELD) + ", SENDER = "
          + ack.get(PBFTMessage.REPLICAIDSENDERFIELD) + " RECEIVER = "
          + ack.get(PBFTMessage.REPLICAIDRECEIVERFIELD) + "> at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );

        getProtocol().perform(new BufferChangeViewAckAction(ack));

        if(checkChangeViewAck(ack)){

            getProtocol().perform(new ExecuteChangeViewRoundThreeAction());
            
        }
        
    }

    private boolean checkChangeViewAck(PBFTMessage ack) {

        if(checkDigest(ack)){
            return gotQuorum(ack);
        }

        return false;
    }

    private boolean gotQuorum(PBFTMessage cv) {
        return ((PBFT)getProtocol()).gotQuorum(cv);
    }

}
