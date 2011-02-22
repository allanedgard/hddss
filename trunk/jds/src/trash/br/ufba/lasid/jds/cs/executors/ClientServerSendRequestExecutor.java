/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.cs.executors;

import trash.br.ufba.lasid.jds.Action;
import trash.br.ufba.lasid.jds.Executor;
import trash.br.ufba.lasid.jds.DistributedProtocol;

/**
 *
 * @author aliriosa
 */
public class ClientServerSendRequestExecutor extends Executor{

    public ClientServerSendRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        //System.out.println("[DistributedProtocol] call ClientServerSendRequestExecutor.execute");
/*        Wrapper wrapper = act.getWrapper();

        ClientServerMessage m = (ClientServerMessage) wrapper;

//        m.put(ClientServerMessage.TYPEFIELD, ClientServerMessage.TYPE.RECEIVEREQUEST);
//        m.put(ClientServerMessage.PAYLOADFIELD, wrapper);
        
        getProtocol().getCommunicator().unicast(
            m, getProtocol().getRemoteProcess()
        );

    }
 * 
 */}

}
