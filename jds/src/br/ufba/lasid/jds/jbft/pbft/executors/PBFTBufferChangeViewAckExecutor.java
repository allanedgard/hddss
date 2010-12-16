/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTBufferChangeViewAckExecutor extends PBFTServerExecutor{

    public PBFTBufferChangeViewAckExecutor(DistributedProtocol protocol) {
        super(protocol);
    }


    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage) act.getWrapper();
        Buffer buffer = ((PBFT)getProtocol()).getChangeViewAckBuffer();

        if(!isServerAuthenticated(m)){
            System.out.println(getDefaultSecurityExceptionMessage(m, "buffer change view ack executor"));
            return;
        }

        /* check if change view ack exists in the buffer */

        if(PBFT.isABufferedMessage(buffer, m)){
            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "has already bufferred the <CHANGE-VIEW-ACK,  view = "
              + m.get(PBFTMessage.VIEWFIELD) + ", digest = "
              + m.get(PBFTMessage.DIGESTFIELD) + ", SENDER = "
              + m.get(PBFTMessage.REPLICAIDSENDERFIELD) + ", RECEIVER = "
              + m.get(PBFTMessage.REPLICAIDRECEIVERFIELD) + ">"
            );

            return;
        }

        /* add change to change view ack buffer */
        buffer.add(m);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "has bufferred the <CHANGE-VIEW-ACK,  view = "
          + m.get(PBFTMessage.VIEWFIELD) + ", digest = "
          + m.get(PBFTMessage.DIGESTFIELD) + ", SENDER = "
          + m.get(PBFTMessage.REPLICAIDSENDERFIELD) + ", RECEIVER = "
          + m.get(PBFTMessage.REPLICAIDRECEIVERFIELD) + "> at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );

    }
    

}
