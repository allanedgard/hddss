/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerSendRequestExecutor;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTSendRequestExecutor extends ClientServerSendRequestExecutor{

    public PBFTSendRequestExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        /*
            [TODO] 1 - this executor has to get the protocol context and resend 
            the request  if  the request timeout  expires. We  can  use adaptive
            timeouts with one single timer that check all requests with response
            out of time. The feedback control approach can be used  in this one.
         *  2 - Requests are encrypted before be multicasted to server group.
         */
        
        PBFTMessage m = (PBFTMessage) act.getMessage();
        
        Group g = (Group) m.getDestination();
        m.setType(PBFTMessage.TYPE.RECEIVEREQUEST);

        protocol.getCommunicator().multicast(m, g);
    }


}
