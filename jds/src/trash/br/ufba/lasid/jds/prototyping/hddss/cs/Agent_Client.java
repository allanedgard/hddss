/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.prototyping.hddss.cs;

import trash.br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.BaseProcess;
import br.ufba.lasid.jds.cs.IClient;
import trash.br.ufba.lasid.jds.cs.actions.CreateRequestAction;
import trash.br.ufba.lasid.jds.cs.actions.ReceiveReplyAction;
import trash.br.ufba.lasid.jds.cs.actions.SendRequestAction;
import trash.br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import trash.br.ufba.lasid.jds.cs.executors.ClientServerCreateRequestExecutor;
import trash.br.ufba.lasid.jds.cs.executors.ClientServerReceiveReplyExecutor;
import trash.br.ufba.lasid.jds.cs.executors.ClientServerSendRequestExecutor;
import br.ufba.lasid.jds.prototyping.hddss.Message;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class Agent_Client extends Agent_ServiceComponent implements IClient<Integer>{
    BaseProcess<Integer> server = new BaseProcess<Integer>();

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

    public BaseProcess<Integer> getServerProcessAddressRef(){
        return this.server;
    }

    public void setServerAddress(String addr){
        server.setID(new Integer(addr));
        //getProtocol().setRemoteProcess(server);
    }

    public void receiveResult(IPayload content) {
        //do nothing
    }

}
