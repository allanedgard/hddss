/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendChangeViewAckAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

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
        PBFTMessage cv = (PBFTMessage)act.getWrapper();

        getProtocol().perform(new BufferChangeViewAction(cv));
        getProtocol().perform(new SendChangeViewAckAction(cv));

    }





}
