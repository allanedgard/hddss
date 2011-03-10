/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.cs.executors;

import trash.br.ufba.lasid.jds.Action;
import trash.br.ufba.lasid.jds.Executor;
import trash.br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.IServer;
import trash.br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class ClientServerServerExecuteRequestExecutor extends Executor{

    IServer server;

    public ClientServerServerExecuteRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    public void setServer(IServer server){
        this.server = server;
    }

    public IServer getServer(){
        return this.server;
    }
    
    @Override
    public synchronized void execute(Action act) {
        //System.out.println("[DistributedProtocol] call ClientServerServerExecuteRequestExecutor.execute");
        ClientServerMessage m = (ClientServerMessage)act.getWrapper();

        m.setContent(server.executeCommand((IPayload)m.getContent()));
        
        //m.setType(ClientServerMessage.TYPE.SENDREPLY);
//        m.put(ClientServerMessage.TYPEFIELD, ClientServerMessage.TYPE.SENDREPLY);

//        IProcess client =  (IProcess)m.get(ClientServerMessage.SOURCEFIELD);
        
  //      m.put(ClientServerMessage.DESTINATIONFIELD, client);
  //      m.put(ClientServerMessage.SOURCEFIELD, server);

        getProtocol().doAction(m);
        
    }

}
