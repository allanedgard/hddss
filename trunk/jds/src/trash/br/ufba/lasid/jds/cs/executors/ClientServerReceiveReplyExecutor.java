/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.cs.executors;

import trash.br.ufba.lasid.jds.Action;
import trash.br.ufba.lasid.jds.Executor;
import trash.br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.IClient;
import trash.br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import br.ufba.lasid.jds.util.IPayload;

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
        IClient client = (IClient)protocol.getLocalProcess();
        client.receiveResult((IPayload)m.getContent());
    }

}
