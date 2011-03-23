/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.prototyping.hddss.cs;

import trash.br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.BaseProcess;
import br.ufba.lasid.jds.cs.IServer;
import trash.br.ufba.lasid.jds.cs.actions.ExecuteRequestAction;
import trash.br.ufba.lasid.jds.cs.actions.ReceiveRequestAction;
import trash.br.ufba.lasid.jds.cs.actions.SendReplyAction;
import trash.br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import trash.br.ufba.lasid.jds.cs.executors.ClientServerReceiveRequestExecutor;
import trash.br.ufba.lasid.jds.cs.executors.ClientServerSendReplyExecutor;
import trash.br.ufba.lasid.jds.cs.executors.ClientServerServerExecuteRequestExecutor;
import br.ufba.lasid.jds.prototyping.hddss.Message;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class Agent_Server extends Agent_ServiceComponent implements IServer<Integer>{
    BaseProcess<Integer> client = new BaseProcess<Integer>();

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
        getProtocol().addExecutor(ExecuteRequestAction.class, newClientServerServerExecuteExecutor());
        getProtocol().addExecutor(SendReplyAction.class, newClientServerSendReplyExecutor());
    }

    public Executor newClientServerReceiveRequestExecutor(){
        return new ClientServerReceiveRequestExecutor(getProtocol());
    }

    public Executor newClientServerServerExecuteExecutor(){
        ClientServerServerExecuteRequestExecutor exec = new ClientServerServerExecuteRequestExecutor(getProtocol());
        exec.setServer(this);
        return exec;
    }

    public Executor newClientServerSendReplyExecutor(){
        return new ClientServerSendReplyExecutor(proto);
    }

    public BaseProcess<Integer> getClientProcessAddressRef(){
        return this.client;
    }

    public IPayload executeCommand(IPayload arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
