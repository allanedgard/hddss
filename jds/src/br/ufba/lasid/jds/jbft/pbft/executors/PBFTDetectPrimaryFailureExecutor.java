/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteChangeViewRoundOneAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTDetectPrimaryFailureExecutor extends PBFTServerExecutor{

    public PBFTDetectPrimaryFailureExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage m = (PBFTMessage) act.getWrapper();

        if(!((PBFT)getProtocol()).existsPrePrepareForRequest(m)){

            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID() + "] "
              + "has detected failure of the primary server at time "
              + ((PBFT)getProtocol()).getTimestamp() + ". "
              + "A change view procedure is going to be trigged."
            );

            getProtocol().perform(new ExecuteChangeViewRoundOneAction());
            
        }
    }



}
