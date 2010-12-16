/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.NewViewAction;

/**
 *
 * @author aliriosa
 */
public class PBFTExecuteChangeViewRoundThreeExecutor extends PBFTServerExecutor{

    public PBFTExecuteChangeViewRoundThreeExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "is going to execute change view round three at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );

        getProtocol().perform(new NewViewAction());
        
    }
    


}
