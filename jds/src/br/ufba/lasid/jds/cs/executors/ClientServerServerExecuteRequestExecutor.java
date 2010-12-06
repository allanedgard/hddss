/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.Server;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import br.ufba.lasid.jds.Process;

/**
 *
 * @author aliriosa
 */
public class ClientServerServerExecuteRequestExecutor extends Executor{

    Server server;

    public ClientServerServerExecuteRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    public void setServer(Server server){
        this.server = server;
    }

    public Server getServer(){
        return this.server;
    }
    
    @Override
    public synchronized void execute(Action act) {
        //System.out.println("[DistributedProtocol] call ClientServerServerExecuteRequestExecutor.execute");
        ClientServerMessage m = (ClientServerMessage)act.getWrapper();

        m.setContent(server.doService(m.getContent()));
        
        //m.setType(ClientServerMessage.TYPE.SENDREPLY);
        m.put(ClientServerMessage.TYPEFIELD, ClientServerMessage.TYPE.SENDREPLY);

        Process client =  (Process)m.get(ClientServerMessage.SOURCEFIELD);
        
        m.put(ClientServerMessage.DESTINATIONFIELD, client);
        m.put(ClientServerMessage.SOURCEFIELD, server);

        getProtocol().doAction(m);
        
    }

}
