/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestScheduler;

/**
 *
 * @author aliriosa
 */
public class PBFTScheduleBacthEndExecutor extends PBFTServerExecutor{

    public PBFTScheduleBacthEndExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage m = (PBFTMessage) act.getWrapper();

        PBFTRequestScheduler scheduler =
                (PBFTRequestScheduler)((PBFT)getProtocol()).getBatchingScheduler();

        long batchTimeout   = ((PBFT)getProtocol()).getBatchingTimeout();
        long batchTimestamp = ((PBFT)getProtocol()).getTimestamp().longValue();
        long time = batchTimeout + batchTimestamp;
                
        m.put(scheduler.getTAG(), ((PBFT)getProtocol()).getBatchingTimeout());
        
        scheduler.schedule(m, time);
    }

    

}
