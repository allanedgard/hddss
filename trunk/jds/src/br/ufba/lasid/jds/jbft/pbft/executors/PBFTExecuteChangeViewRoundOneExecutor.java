/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.CreateChangeViewAction;

/**
 *
 * @author aliriosa
 */
public class PBFTExecuteChangeViewRoundOneExecutor extends PBFTServerExecutor{

    public PBFTExecuteChangeViewRoundOneExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "is going to execute change view round one at time "
              + ((PBFT)getProtocol()).getTimestamp()
            );

        /* create and buffer a change view message */
        getProtocol().perform(new CreateChangeViewAction());

        /* multicasts the last change view buffered to the group */
        //getProtocol().perform(new SendChangeViewAction());
    }

    

}
