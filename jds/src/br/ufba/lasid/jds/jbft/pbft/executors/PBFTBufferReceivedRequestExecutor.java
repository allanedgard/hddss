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
public class PBFTBufferReceivedRequestExecutor extends PBFTServerExecutor{

    public PBFTBufferReceivedRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage m = (PBFTMessage)act.getWrapper();

        Buffer buffer = ((PBFT)getProtocol()).getRequestBuffer();

        if(!isServerAuthenticated(m)){
            System.out.println(getDefaultSecurityExceptionMessage(m, "buffer"));
            return;
        }

        /* check if request exists in the buffer */

        if(PBFT.isABufferedMessage(buffer, m)){
            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "has already bufferred " + m.getContent()
            );

            return;            
        }
        
        /* add request to request buffer */
        buffer.add(m);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "bufferred " + m.getContent() + " at time " + ((PBFT)getProtocol()).getTimestamp()
        );

    }

}
