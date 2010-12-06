/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.executors.ClientServerSendRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTSendRequestExecutor extends ClientServerSendRequestExecutor{

    public PBFTSendRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage m = (PBFTMessage) act.getWrapper();
        m.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVEREQUEST);

        System.out.println(
            "client [p" + getProtocol().getLocalProcess().getID()+"] "
          + "broadcasts " + m.getContent() + " to group " + getProtocol().getRemoteProcess().getID()
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
       );

        getProtocol().getCommunicator().multicast(m, getProtocol().getRemoteProcess());

        
    }

}

