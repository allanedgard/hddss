/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BatchTimeoutAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteStartNewRoundPhaseOneAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequestMessage;
import br.ufba.lasid.jds.security.Authenticator;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveRequestExecutor extends PBFTServerExecutor{

    PBFTMessage batch = null;
    
    public PBFTReceiveRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    public boolean isABatchTimeoutAction(Action act){
        return BatchTimeoutAction.class.equals(act.getClass());
    }
    
    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage)act.getWrapper();
        
       System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "received " + m.getContent() + " at time " + ((PBFT)getProtocol()).getTimestamp()
       );
       Authenticator authenticator =
                ((PBFT)getProtocol()).getClientMessageAuthenticator();

        if(!authenticator.check(m)){
           System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "rejected  " + m.getContent() + " at time " + ((PBFT)getProtocol()).getTimestamp()
              + "because it wasn't authenticated"
           );

           return;
        }
       
        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "authenticated  " + m.getContent()
          + "at time " + ((PBFT)getProtocol()).getTimestamp()
        );

        PBFTMessage request = new PBFTRequestMessage();
        request.putAll(m);

        request = encrypt(request);

        getProtocol().perform(new ExecuteStartNewRoundPhaseOneAction(request));

    }
}
