/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.SingleProcess;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewAckMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTSendChangeViewAckExecutor extends PBFTServerExecutor{

    public PBFTSendChangeViewAckExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage cv = (PBFTMessage) act.getWrapper();
        PBFTMessage ack = makeChangeViewAck(cv);

    }

    private PBFTMessage makeChangeViewAck(PBFTMessage cv) {

        PBFTMessage ack = new PBFTChangeViewAckMessage();
        Object destID = cv.get(PBFTMessage.REPLICAIDFIELD);
        
        ack.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVECHANGEVIEWACK);
        ack.put(PBFTMessage.REPLICAIDSENDERFIELD, getProtocol().getLocalProcess().getID());
        ack.put(PBFTMessage.REPLICAIDRECEIVERFIELD, destID);
        ack.put(PBFTMessage.VIEWFIELD, cv.get(PBFTMessage.VIEWFIELD));

        SingleProcess p = new SingleProcess();

        p.setID(destID);

        ack = encrypt(ack);
        ack = makeDisgest(ack);

        getProtocol().getCommunicator().unicast(ack, p);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "has sent the <CHANGE-VIEW-ACK,  view = "
          + ack.get(PBFTMessage.VIEWFIELD) + ", digest = "
          + ack.get(PBFTMessage.DIGESTFIELD) + ", SENDER = "
          + ack.get(PBFTMessage.REPLICAIDSENDERFIELD) + ", RECEIVER = "
          + ack.get(PBFTMessage.REPLICAIDRECEIVERFIELD) + "> at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );


        return ack;
    }





}
