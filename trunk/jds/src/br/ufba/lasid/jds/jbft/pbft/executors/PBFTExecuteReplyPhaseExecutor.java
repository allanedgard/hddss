/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.actions.ExecuteRequestAction;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferCommittedRequestAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTExecuteReplyPhaseExecutor extends PBFTServerExecutor{

    public PBFTExecuteReplyPhaseExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getWrapper();
        PBFTMessage batch = (PBFTMessage) m.get(PBFTMessage.REQUESTFIELD);
       System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "started the reply phase of the protocol at time "
          + ((PBFT)getProtocol()).getTimestamp()
       );

       getProtocol().perform(new BufferCommittedRequestAction(batch));
       getProtocol().perform(new ExecuteRequestAction(batch));
    }




}
