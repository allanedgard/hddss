/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteChangeViewRoundOneAction;

/**
 *
 * @author aliriosa
 */
public class PBFTRetransmiteChangeViewExecutor extends PBFTServerExecutor{

    public PBFTRetransmiteChangeViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        ((PBFT)getProtocol()).incViewChangeAttemps();

            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "has triggered restramition of the view-change procedure "
              + "at time " + ((PBFT)getProtocol()).getTimestamp()
            );


        getProtocol().perform(new ExecuteChangeViewRoundOneAction());
    }




}
