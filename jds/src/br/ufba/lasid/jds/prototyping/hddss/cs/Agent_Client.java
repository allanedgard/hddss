/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.cs;

import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.SingleProcess;
import br.ufba.lasid.jds.cs.Client;
import br.ufba.lasid.jds.cs.actions.CreateRequestAction;
import br.ufba.lasid.jds.cs.actions.ReceiveReplyAction;
import br.ufba.lasid.jds.cs.actions.SendRequestAction;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import br.ufba.lasid.jds.cs.executors.ClientServerCreateRequestExecutor;
import br.ufba.lasid.jds.cs.executors.ClientServerReceiveReplyExecutor;
import br.ufba.lasid.jds.cs.executors.ClientServerSendRequestExecutor;
import br.ufba.lasid.jds.prototyping.hddss.Message;

/**
 *
 * @author aliriosa
 */
public class Agent_Client extends Agent_ServiceComponent implements Client<Integer>{
    SingleProcess<Integer> server = new SingleProcess<Integer>();

    @Override
    public void receive(Message msg) {
        ClientServerMessage m = (ClientServerMessage)msg.getContent();
        getProtocol().doAction(m);
    }

    @Override
    public void setup() {
        super.setup();

        getProtocol().addExecutor(CreateRequestAction.class, newCreateRequestExecutor());
        getProtocol().addExecutor(SendRequestAction.class, newSendRequestExecutor());
        getProtocol().addExecutor(ReceiveReplyAction.class, newReceiveReplyExecutor());
    }

    public Executor newCreateRequestExecutor(){
        return new ClientServerCreateRequestExecutor(getProtocol());
    }

    public Executor newSendRequestExecutor(){
        return new ClientServerSendRequestExecutor(getProtocol());
    }

    public Executor newReceiveReplyExecutor(){
        return new ClientServerReceiveReplyExecutor(getProtocol());
    }

    public SingleProcess<Integer> getServerProcessAddressRef(){
        return this.server;
    }

    public void setServerAddress(String addr){
        server.setID(new Integer(addr));
        getProtocol().setRemoteProcess(server);
    }

    public void receiveReply(Object content) {
        //do nothing
    }

}
