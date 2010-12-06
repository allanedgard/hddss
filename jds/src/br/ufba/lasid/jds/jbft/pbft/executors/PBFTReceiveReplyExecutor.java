/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.Client;
import br.ufba.lasid.jds.cs.executors.ClientServerReceiveReplyExecutor;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestRetransmistionScheduler;
import br.ufba.lasid.jds.security.Authenticator;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveReplyExecutor extends ClientServerReceiveReplyExecutor{

    public PBFTReceiveReplyExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage m = ((PBFTMessage) act.getWrapper());

        if(ckeckReply(m)){
            Client client = (Client)getProtocol().getLocalProcess();
            client.receiveReply(m.getContent());
            PBFTRequestRetransmistionScheduler scheduler =
                    (PBFTRequestRetransmistionScheduler)((PBFT)getProtocol()).getClientScheduler();
            
            scheduler.cancel(m);

            RemoveFromBuffer(m);
        }

    }

    public boolean ckeckReply(PBFTMessage m){
        if(isValidReply(m)){            
            return gotQuorum(m);
        }
        return true;
    }
    
    public synchronized void RemoveFromBuffer(PBFTMessage m){
        Buffer buffer = ((PBFT)getProtocol()).getRequestBuffer();
        
        if(PBFT.isABufferedMessage(buffer, m)){
            PBFTMessage req = PBFT.getBufferedMessage(buffer, m);
            buffer.remove(req);
            req.put(PBFTMessage.TIMESTAMPFIELD, new Long(-1));
//            System.gc();
        }        
    }
    public boolean isValidReply(PBFTMessage m){
        Authenticator authenticator =
        ((PBFT)getProtocol()).getClientMessageAuthenticator();

        if(!authenticator.check(m)){
            return false;
        }

        return (authenticator.chechDisgest(m) && existsRequest(m));

    }

    private boolean existsRequest(PBFTMessage m) {
        return ((PBFT)getProtocol()).existsRequest(m);
    }

    private boolean gotQuorum(PBFTMessage m) {
        return ((PBFT)getProtocol()).gotQuorum(m);
    }

}
