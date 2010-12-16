/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.HandleBatchAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ScheduleBacthEndAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ScheduleNewViewAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTExecuteStartNewRoundPhaseTwoExecutor extends PBFTServerExecutor{

    public PBFTExecuteStartNewRoundPhaseTwoExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage) act.getWrapper();
        
        if(!isServerAuthenticated(m)){
            System.out.println(getDefaultSecurityExceptionMessage(m, "phase two"));
            return;
        }

        /**
         * if primary then perform a batching request else schedules a
         * new view action.
         */

        if(((PBFT)getProtocol()).isPrimary()){

            /* performs batching of the request */
            getProtocol().perform(new HandleBatchAction(m));
            getProtocol().perform(new ScheduleBacthEndAction(m));

        }else{

            /* performs schedule of the change view*/
            getProtocol().perform(new ScheduleNewViewAction(m));
            
        }

    }




}
