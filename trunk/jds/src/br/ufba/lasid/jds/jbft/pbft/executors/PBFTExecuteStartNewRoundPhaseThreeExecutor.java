/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCurrentRoundPhaseOneAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTExecuteStartNewRoundPhaseThreeExecutor extends PBFTServerExecutor{

    public PBFTExecuteStartNewRoundPhaseThreeExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage m = (PBFTMessage) act.getWrapper();

        if(!isServerAuthenticated(m)){
            System.out.println(getDefaultSecurityExceptionMessage(m, "phase three"));
            return;
        }

        if(((PBFT)getProtocol()).isPrimary()){

            getProtocol().perform(new ExecuteCurrentRoundPhaseOneAction(m));
        }        
    }





}
