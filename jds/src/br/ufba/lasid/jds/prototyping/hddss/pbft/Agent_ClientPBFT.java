/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.cs.actions.CreateRequestAction;
import br.ufba.lasid.jds.cs.actions.ReceiveReplyAction;
import br.ufba.lasid.jds.cs.actions.SendRequestAction;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTClient;
import br.ufba.lasid.jds.jbft.pbft.actions.RetransmiteRequestAction;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTRetransmiteRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCreateRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTReceiveReplyExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTScheculeRequestRetransmissionExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTSendRequestExecutor;

/**
 *
 * @author aliriosa
 */
public class Agent_ClientPBFT extends Agent_PBFT implements PBFTClient<Integer>{
    
    @Override
    public void setup() {
        super.setup();
        getProtocol().addExecutor(CreateRequestAction.class, newCreateRequestExecutor());
        getProtocol().addExecutor(SendRequestAction.class, newSendRequestExecutor());
        getProtocol().addExecutor(SendRequestAction.class, newScheculeRequestRetransmissionExecutor());
        getProtocol().addExecutor(ReceiveReplyAction.class, newReceiveReplyExecutor());
        getProtocol().addExecutor(RetransmiteRequestAction.class, newRetransmiteRequestExecutor());
    }


    public void receiveReply(Object content) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Executor newScheculeRequestRetransmissionExecutor(){
        return new PBFTScheculeRequestRetransmissionExecutor(getProtocol());
    }
    public Executor newCreateRequestExecutor() {
        return new PBFTCreateRequestExecutor(getProtocol());
    }

    public Executor newSendRequestExecutor() {
        return new PBFTSendRequestExecutor(getProtocol());
    }

    public Executor newReceiveReplyExecutor() {
        return new PBFTReceiveReplyExecutor(getProtocol());
    }

    public Executor newRetransmiteRequestExecutor(){
        return new PBFTRetransmiteRequestExecutor(getProtocol());
    }
    public void setClientRetransmissionTimeout(String timeout){
        getProtocol().getContext().put(PBFT.CLIENTRETRANSMISSIONTIMEOUT, new Long(timeout));
    }
}
