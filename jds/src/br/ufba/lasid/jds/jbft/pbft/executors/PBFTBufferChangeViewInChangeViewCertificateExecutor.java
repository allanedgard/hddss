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
public class PBFTBufferChangeViewInChangeViewCertificateExecutor extends PBFTServerExecutor{

    public PBFTBufferChangeViewInChangeViewCertificateExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage) act.getWrapper();
        Buffer buffer = ((PBFT)getProtocol()).getChangeViewCertificate();

        /* check if change view exists in the buffer */

        if(PBFT.isABufferedMessage(buffer, m)){
            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "has already bufferred in change view certificate S the change "
              + "view <CHANGE-VIEW,  view = "
              + m.get(PBFTMessage.VIEWFIELD) + ", digest = "
              + m.get(PBFTMessage.DIGESTFIELD) + ", CHKPOINTNUMBER = "
              + m.get(PBFTMessage.CHECKPOINTNUMBERFIELD) + ", P, Q, replica = "
              + m.get(PBFTMessage.REPLICAIDFIELD) + ">"
            );

            return;
        }

        /* add change to change view certificate buffer */
        buffer.add(m);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "has bufferred in change view certificate the <CHANGE-VIEW,  view = "
          + m.get(PBFTMessage.VIEWFIELD) + ", digest = "
          + m.get(PBFTMessage.DIGESTFIELD) + ", CHKPOINTNUMBER = "
          + m.get(PBFTMessage.CHECKPOINTNUMBERFIELD) + ", P, Q, replica = "
          + m.get(PBFTMessage.REPLICAIDFIELD) + "> at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );

    }
    


}
