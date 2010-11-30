/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.cs.actions.ReceiveReplyAction;
import br.ufba.lasid.jds.cs.actions.SendRequestAction;
import br.ufba.lasid.jds.jbft.pbft.PBFTClient;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTSendRequestExecutor;

/**
 *
 * @author aliriosa
 */
public class Agent_ClientPBFT extends Agent_PBFT implements PBFTClient<Integer>{
    
    @Override
    public void setup() {
        super.setup();
        getProtocol().addExecutor(SendRequestAction.class, newClientServerSendRequestExecutor());
        getProtocol().addExecutor(ReceiveReplyAction.class, newClientServerReceiveReplyExecutor());
    }


    public void receiveReply(Object content) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Executor newClientServerSendRequestExecutor() {
        return new PBFTSendRequestExecutor(getProtocol());
    }

    public Executor newClientServerReceiveReplyExecutor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
}
