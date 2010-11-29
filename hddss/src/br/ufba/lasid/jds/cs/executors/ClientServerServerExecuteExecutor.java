/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.Server;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import br.ufba.lasid.jds.Process;

/**
 *
 * @author aliriosa
 */
public class ClientServerServerExecuteExecutor extends Executor{

    Server server;

    public ClientServerServerExecuteExecutor(Protocol protocol) {
        super(protocol);
    }

    public void setServer(Server server){
        this.server = server;
    }
    
    @Override
    public synchronized void execute(Action act) {
        //System.out.println("[Protocol] call ClientServerServerExecuteExecutor.execute");
        ClientServerMessage m = (ClientServerMessage)act.getMessage();

        m.setContent(server.doService(m.getContent()));
        
        m.setType(ClientServerMessage.TYPE.SENDREPLY);

        Process client =  m.getSource();
        
        m.setDestination(client);
        m.setSource(server);

        protocol.doAction(m);
        
    }

}
