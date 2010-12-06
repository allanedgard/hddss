/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestRetransmistionScheduler;

/**
 *
 * @author aliriosa
 */
public class PBFTScheculeRequestRetransmissionExecutor extends Executor{

    public PBFTScheculeRequestRetransmissionExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage m = (PBFTMessage) act.getWrapper();

        Long timeout   = ((PBFT)getProtocol()).getRetransmissionTimeout();

        Long timestamp =((PBFT)getProtocol()).getTimestamp();

        long rttime = new Long(timestamp.intValue() + timeout.longValue());
        

        PBFTRequestRetransmistionScheduler scheduler =
                (PBFTRequestRetransmistionScheduler)(((PBFT)getProtocol()).getClientScheduler());

        scheduler.schedule(m, rttime);

//        ((PBFT)getProtocol()).getDebugger().debug(
//            "["+ getClass().getSimpleName()+ ".scheduleRetransmission] "
//          + "scheduling of (" + m + ") for time " + rttime
//         );
        
        System.out.println(
            "client [p" + getProtocol().getLocalProcess().getID()+"] "
            + "scheduled the retransmittion of " + m.getContent() + " to group "
            + getProtocol().getRemoteProcess().getID()
            + " for the time " + rttime
        );
        
    }


    
}
