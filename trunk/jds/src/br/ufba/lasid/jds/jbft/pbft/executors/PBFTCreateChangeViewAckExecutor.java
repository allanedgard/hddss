/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.SingleProcess;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferChangeViewAckAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewAckMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTCreateChangeViewAckExecutor extends PBFTServerExecutor{

    public PBFTCreateChangeViewAckExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage) act.getWrapper();
        PBFTMessage cv = ((PBFT)getProtocol()).getChangeViewSentFromSelectedPrimary(m);        
        PBFTMessage ack = makeChangeViewAck(cv);

        getProtocol().perform(new BufferChangeViewAckAction(ack));
        
    }

    private PBFTMessage makeChangeViewAck(PBFTMessage cv) {

        PBFTMessage   ack       = new PBFTChangeViewAckMessage();
        SingleProcess primary   = new SingleProcess();
        
        primary.setID(cv.get(PBFTMessage.REPLICAIDFIELD));

        ack.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVECHANGEVIEWACK);
        ack.put(PBFTMessage.VIEWFIELD, cv.get(PBFTMessage.VIEWFIELD));
        ack.put(PBFTMessage.REPLICAIDSENDERFIELD, getProtocol().getLocalProcess().getID());
        ack.put(PBFTMessage.REPLICAIDRECEIVERFIELD, primary.getID());
        ack.put(PBFTMessage.DIGESTFIELD, cv.get(PBFTMessage.DIGESTFIELD));

        ack = encrypt(ack);
        
        getProtocol().getCommunicator().unicast(ack, primary);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "sent <CHANGE-VIEW-ACK, view = " + ack.get(PBFTMessage.VIEWFIELD) 
          + ", sender = " + ack.get(PBFTMessage.REPLICAIDSENDERFIELD)
          + ", receiver = " + ack.get(PBFTMessage.REPLICAIDRECEIVERFIELD)
          + ", digest = " + ack.get(PBFTMessage.DIGESTFIELD) + "> "
          + "at time " + ((PBFT)getProtocol()).getTimestamp()
        );


        return ack;

    }

}
