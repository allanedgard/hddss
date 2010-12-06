/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;

/**
 *
 * @author aliriosa
 */
public class PBFTSendCheckpointExecutor extends Executor{
    public PBFTSendCheckpointExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

/*        int checkpointPeriod = ((PBFT)getProtocol()).getCheckPointPeriod().intValue();

        count++;

        if((count % checkpointPeriod) == 0){
            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTSendCheckPointRequestExecutor.execute] the last sequence "
                + "number commited in process (p"+getProtocol().getLocalProcess().getID()+") "
                + "is " + ((PBFT)getProtocol()).getLastCommitedSequenceNumber()
             );
        }
*/
    }



}
