/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.SendCommitAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTBufferCommitExecutor extends PBFTServerExecutor{

    public PBFTBufferCommitExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage) act.getWrapper();
        Buffer buffer = ((PBFT)getProtocol()).getCommitBuffer();

        if(!isServerAuthenticated(m)){
            System.out.println(getDefaultSecurityExceptionMessage(m, "buffer commit executor"));
            return;
        }

        /* check if commit exists in the buffer */

        if(PBFT.isABufferedMessage(buffer, m)){
            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "has already bufferred the commit (" + m.get(PBFTMessage.SEQUENCENUMBERFIELD) + ")"
            );

            return;
        }

        /* add commit to commit buffer */
        buffer.add(m);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "bufferred commit (" + m.get(PBFTMessage.SEQUENCENUMBERFIELD) + ") "
          + "at time " + ((PBFT)getProtocol()).getTimestamp()
        );

        getProtocol().perform(new SendCommitAction(m));

    }
    

}
