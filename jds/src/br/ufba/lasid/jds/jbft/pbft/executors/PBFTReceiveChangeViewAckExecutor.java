/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
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
          + ack.get(PBFTMessage.DIGESTFIELD) + ", CHKPOINTNUMBER = "
          + ack.get(PBFTMessage.REPLICAIDSENDERFIELD) + ", P, Q, replica = "
          + ack.get(PBFTMessage.REPLICAIDRECEIVERFIELD) + "> at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );
        
    }

    

}
