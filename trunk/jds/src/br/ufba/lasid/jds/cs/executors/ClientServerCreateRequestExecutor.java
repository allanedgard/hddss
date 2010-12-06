/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class ClientServerCreateRequestExecutor extends Executor{

    public ClientServerCreateRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        Wrapper wrapper = act.getWrapper();
        ClientServerMessage m = new ClientServerMessage();
        m.put(ClientServerMessage.TYPEFIELD, ClientServerMessage.TYPE.SENDREQUEST);
        m.put(ClientServerMessage.PAYLOADFIELD, wrapper);
        m.put(ClientServerMessage.SOURCEFIELD, getProtocol().getLocalProcess());
        m.put(ClientServerMessage.DESTINATIONFIELD, getProtocol().getRemoteProcess());

        getProtocol().doAction(m);

    }


    
}
