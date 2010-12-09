/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;

/**
 *
 * @author aliriosa
 */
public class ClientServerSendReplyExecutor extends Executor{

    public ClientServerSendReplyExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        //System.out.println("[DistributedProtocol] call ClientServerSendReplyExecutor.execute");
        ClientServerMessage m = ((ClientServerMessage) act.getWrapper());
        m.put(ClientServerMessage.TYPEFIELD, ClientServerMessage.TYPE.RECEIVEREPLY);
//        m.setType(ClientServerMessage.TYPE.RECEIVEREPLY);

        getProtocol().getCommunicator().unicast(
             (ClientServerMessage) act.getWrapper(),
             (br.ufba.lasid.jds.Process)m.get(ClientServerMessage.DESTINATIONFIELD)
        );
    }


}