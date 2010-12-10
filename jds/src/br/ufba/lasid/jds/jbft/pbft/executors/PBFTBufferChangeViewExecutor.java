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
public class PBFTBufferChangeViewExecutor extends PBFTServerExecutor{

    public PBFTBufferChangeViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage) act.getWrapper();
        Buffer buffer = ((PBFT)getProtocol()).getChangeViewBuffer();

        if(!isServerAuthenticated(m)){
            System.out.println(getDefaultSecurityExceptionMessage(m, "buffer change view executor"));
            return;
        }

        /* check if change view exists in the buffer */

        if(PBFT.isABufferedMessage(buffer, m)){
            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "has already bufferred the <CHANGE-VIEW,  view = "
              + m.get(PBFTMessage.VIEWFIELD) + ", digest = " 
              + m.get(PBFTMessage.DIGESTFIELD) + ", CHKPOINTNUMBER = "
              + m.get(PBFTMessage.CHECKPOINTNUMBERFIELD) + ", P, Q, replica = "
              + m.get(PBFTMessage.REPLICAIDFIELD) + ">"
            );

            return;
        }

        /* add change to commit buffer */
        buffer.add(m);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "has bufferred the <CHANGE-VIEW,  view = "
          + m.get(PBFTMessage.VIEWFIELD) + ", digest = "
          + m.get(PBFTMessage.DIGESTFIELD) + ", CHKPOINTNUMBER = "
          + m.get(PBFTMessage.CHECKPOINTNUMBERFIELD) + ", P, Q, replica = "
          + m.get(PBFTMessage.REPLICAIDFIELD) + "> at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );

    }



}
