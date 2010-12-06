/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.actions.SendReplyAction;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.GarbageCollectionAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendCheckpointAction;

/**
 *
 * @author aliriosa
 */
public class PBFTGarbageCollectionExecutor extends PBFTServerExecutor{
    protected long executedMessageCount = 0;
    public PBFTGarbageCollectionExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    public boolean isTimeToMakeCheckpoint(Action act){

        long checkpointPeriod = ((PBFT)getProtocol()).getCheckPointPeriod();

        /**
         * A checkpoint must be performed when we complete a checkpoint period 
         * (this is, enought request messages has been served) or a garbage col-
         * lection action has been invoked (this is, a garbage collection is
         * performed on-demand).
         */
        if(act instanceof SendReplyAction){

            executedMessageCount++;

            if((executedMessageCount % checkpointPeriod) == 0){

                executedMessageCount = 0;
                return true;

            }
        }

        return (act instanceof GarbageCollectionAction);
        
    }
    
    @Override
    public synchronized void execute(Action act) {
                
        if(isTimeToMakeCheckpoint(act)){
            
            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "starts a new checkpoint procedure at time "
              + ((PBFT)getProtocol()).getTimestamp()
            );

            getProtocol().perform(new SendCheckpointAction());
            
        }
        
    }



}
