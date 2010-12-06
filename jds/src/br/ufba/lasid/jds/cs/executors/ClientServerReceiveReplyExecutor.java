/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.Client;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;

/**
 *
 * @author aliriosa
 */
public class ClientServerReceiveReplyExecutor extends Executor{

    public ClientServerReceiveReplyExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        //System.out.println("[DistributedProtocol] call ClientServerReceiveReplyExecutor.execute");
        ClientServerMessage m = ((ClientServerMessage) act.getWrapper());
        Client client = (Client)protocol.getLocalProcess();
        client.receiveReply(m.getContent());
    }

}
