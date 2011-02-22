/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.cs.executors;

import trash.br.ufba.lasid.jds.Action;
import trash.br.ufba.lasid.jds.Executor;
import trash.br.ufba.lasid.jds.DistributedProtocol;
import trash.br.ufba.lasid.jds.cs.comm.ClientServerMessage;

/**
 *
 * @author aliriosa
 */
public class ClientServerReceiveRequestExecutor extends Executor{

    public ClientServerReceiveRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        //System.out.println("[DistributedProtocol] call ClientServerReceiveRequestExecutor.execute");
        ClientServerMessage m = (ClientServerMessage) act.getWrapper();
        //m.put(ClientServerMessage.TYPEFIELD, ClientServerMessage.TYPE.EXECUTE);
        protocol.doAction(m);
    }

}
