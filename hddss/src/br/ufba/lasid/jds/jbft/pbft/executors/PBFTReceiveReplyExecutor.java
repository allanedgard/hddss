/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.Client;
import br.ufba.lasid.jds.cs.executors.ClientServerReceiveReplyExecutor;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveReplyExecutor extends ClientServerReceiveReplyExecutor{

    public PBFTReceiveReplyExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = ((PBFTMessage) act.getMessage());
        Client client = (Client)protocol.getLocalProcess();
        if(ckeckReply(m)){
            client.receiveReply(m.getContent());
            manageRequests(m);

        }

    }

    public boolean ckeckReply(PBFTMessage m){
        /*
          [TODO] this executor has to ckeck if 3f + 1 replies was received
          before delivery the reply to the client application. After this 
          check the buffered request related to the checked reply must be
          deleted of the buffer. this executor has to work sinchronized with
          the SendRequestExecutor.
            *** (see PBFTReceiveReplyExecutor.manageRequests())
         */

        return true;
    }

    public void manageRequests(PBFTMessage m){
        
    }

}
