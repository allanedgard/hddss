/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.cs;

import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.SingleProcess;
import br.ufba.lasid.jds.cs.Server;
import br.ufba.lasid.jds.cs.actions.ExecuteAction;
import br.ufba.lasid.jds.cs.actions.ReceiveRequestAction;
import br.ufba.lasid.jds.cs.actions.SendReplyAction;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import br.ufba.lasid.jds.cs.executors.ClientServerReceiveRequestExecutor;
import br.ufba.lasid.jds.cs.executors.ClientServerSendReplyExecutor;
import br.ufba.lasid.jds.cs.executors.ClientServerServerExecuteExecutor;
import br.ufba.lasid.jds.prototyping.hddss.Message;

/**
 *
 * @author aliriosa
 */
public class Agent_Server extends Agent_ServiceComponent implements Server<Integer>{
    SingleProcess<Integer> client = new SingleProcess<Integer>();

    public void setClientAddress(String addr){
        client.setID(new Integer(addr));
    }
    
    @Override
    public void receive(Message msg) {
        ClientServerMessage m = (ClientServerMessage)msg.getContent();
        getProtocol().doAction(m);
    }

    @Override
    public void setup() {
        super.setup();
        getProtocol().addExecutor(ReceiveRequestAction.class, newClientServerReceiveRequestExecutor());
        getProtocol().addExecutor(ExecuteAction.class, newClientServerServerExecuteExecutor());
        getProtocol().addExecutor(SendReplyAction.class, newClientServerSendReplyExecutor());
    }

    public Executor newClientServerReceiveRequestExecutor(){
        return new ClientServerReceiveRequestExecutor(getProtocol());
    }

    public Executor newClientServerServerExecuteExecutor(){
        ClientServerServerExecuteExecutor exec = new ClientServerServerExecuteExecutor(getProtocol());
        exec.setServer(this);
        return exec;
    }

    public Executor newClientServerSendReplyExecutor(){
        return new ClientServerSendReplyExecutor(proto);
    }

    public SingleProcess<Integer> getClientProcessAddressRef(){
        return this.client;
    }

    public Object doService(Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
