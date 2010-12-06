/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.SendPrepareAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTBufferPrepareExecutor extends PBFTServerExecutor{

    public PBFTBufferPrepareExecutor(DistributedProtocol protocol) {
        super(protocol);
    }


    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage) act.getWrapper();
        Buffer buffer = ((PBFT)getProtocol()).getPrepareBuffer();

        if(!isServerAuthenticated(m)){
            System.out.println(getDefaultSecurityExceptionMessage(m, "buffer prepare executor"));
            return;
        }

        /* check if preprepare exists in the buffer */

        if(PBFT.isABufferedMessage(buffer, m)){
            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "has already bufferred the prepare (" + m.get(PBFTMessage.SEQUENCENUMBERFIELD) + ")"
            );

            return;
        }

        /* add preprepare to preprepare buffer */
        buffer.add(m);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "bufferred prepare (" + m.get(PBFTMessage.SEQUENCENUMBERFIELD) + ") "
          + "at time " + ((PBFT)getProtocol()).getTimestamp()
        );

        if(!(((PBFT)getProtocol()).isPrimary())){
            getProtocol().perform(new SendPrepareAction(m));
        }

    }



}
