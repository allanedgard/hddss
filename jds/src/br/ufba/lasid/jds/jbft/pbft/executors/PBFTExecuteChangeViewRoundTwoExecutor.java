/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.CreateChangeViewAckAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewAckMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestScheduler;

/**
 *
 * @author aliriosa
 */
public class PBFTExecuteChangeViewRoundTwoExecutor extends PBFTServerExecutor{

    public PBFTExecuteChangeViewRoundTwoExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "is going to execute change view round two at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );

        PBFTMessage cv = (PBFTMessage)act.getWrapper();

        getProtocol().perform(new CreateChangeViewAckAction(cv));

        /* [TODO]
            we must schedule a new change view here ... to prevent primary
            failure and guarantee liveness.
         */

        scheduleNewView();
        
    }

    public void scheduleNewView(){
        
        int attemps = ((PBFT)getProtocol()).getViewChangeAttemps();

        Long timeout   = ((PBFT)getProtocol()).getChangeViewRetransmissionTimeout();

        Long timestamp =((PBFT)getProtocol()).getTimestamp();

        long rttime = timestamp.intValue() + timeout.longValue() * (long)Math.pow(2, (double)attemps);

        PBFTRequestScheduler scheduler =
                (PBFTRequestScheduler)(((PBFT)getProtocol()).getChangeViewRetransmittionScheduler());

        PBFTChangeViewAckMessage ack = new PBFTChangeViewAckMessage();

        ack.put(PBFTMessage.CLIENTFIELD, getProtocol().getLocalProcess());
        ack.put(PBFTMessage.TIMESTAMPFIELD, ((PBFT)getProtocol()).getTimestamp());
        ack.put(scheduler.getTAG(), timeout);
        scheduler.schedule(ack, rttime);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
            + "scheduled the retransmittion of a new-change procedure "
            + " for the time " + rttime
        );
        
    }

}
