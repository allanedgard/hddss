/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteStartNewRoundPhaseThreeAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendPrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBatchMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRejuvenationScheduler;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTRejuvenationExecutor extends PBFTServerExecutor{

    Buffer incomingRequestBuffer = new Buffer();
    
    public PBFTRejuvenationExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
          PBFTRejuvenationScheduler scheduler =
                (PBFTRejuvenationScheduler )((PBFT)getProtocol()).getRejuvenationScheduler();
          // escrever e fazer o agendamento
          

    }
    

}
