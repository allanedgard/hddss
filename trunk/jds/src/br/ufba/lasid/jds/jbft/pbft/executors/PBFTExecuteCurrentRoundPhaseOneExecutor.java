/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.CreatePrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTExecuteCurrentRoundPhaseOneExecutor extends PBFTServerExecutor{

    public PBFTExecuteCurrentRoundPhaseOneExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getWrapper();

        if(!isServerAuthenticated(m)){
            System.out.println(getDefaultSecurityExceptionMessage(m, "new round phase three"));
            return;
        }

       System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "started the phase one of the three phases protocol at time "
          + ((PBFT)getProtocol()).getTimestamp()
       );

        if(((PBFT)getProtocol()).isPrimary()){

            getProtocol().perform(new CreatePrePrepareAction(m));
        }
    }
  
}
