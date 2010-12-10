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
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestScheduler;

/**
 *
 * @author aliriosa
 */
public class PBFTScheduleNewViewExecutor extends Executor{

    public PBFTScheduleNewViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage)act.getWrapper();

        PBFTRequestScheduler scheduler =
                (PBFTRequestScheduler) ((PBFT)getProtocol()).getPrimaryFDScheduler();

        long fdTimeout = ((PBFT)getProtocol()).getPrimaryFaultyTimeout();
        long timestamp = ((PBFT)getProtocol()).getTimestamp().longValue();
        long time = fdTimeout + timestamp;

        m.put(scheduler.getTAG(), fdTimeout);

        scheduler.schedule(m, time);
        
        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
            + "schedules a changing vew for " + m.getContent() + " "
            + " at time " + timestamp
        );

    }





}
