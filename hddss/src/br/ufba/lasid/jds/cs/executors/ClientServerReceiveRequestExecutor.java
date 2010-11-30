/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;

/**
 *
 * @author aliriosa
 */
public class ClientServerReceiveRequestExecutor extends Executor{

    public ClientServerReceiveRequestExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        //System.out.println("[Protocol] call ClientServerReceiveRequestExecutor.execute");
        ClientServerMessage m = (ClientServerMessage) act.getMessage();
        m.put(ClientServerMessage.TYPEFIELD, ClientServerMessage.TYPE.EXECUTE);
        protocol.doAction(m);
    }

}
