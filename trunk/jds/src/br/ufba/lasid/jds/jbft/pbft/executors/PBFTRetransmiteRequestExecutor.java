/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTRetransmiteRequestExecutor extends PBFTSendRequestExecutor{

    public PBFTRetransmiteRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage) act.getWrapper();
        
        m.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.SENDREQUEST);

        System.out.println(
            "client [p" + getProtocol().getLocalProcess().getID() + "] "
          + "executes retransmite action for request with payload " 
          + m.getContent() + " at time " + ((PBFT)getProtocol()).getTimestamp()
        );
        
        getProtocol().doAction(m);
 
    }
}
