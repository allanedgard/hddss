/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BatchRequestAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTHandleBatchExecutor extends PBFTServerExecutor{
    int count = 0;
    public PBFTHandleBatchExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage) act.getWrapper();

        if(!isServerAuthenticated(m)){
            System.out.println(getDefaultSecurityExceptionMessage(m, "prepare batch"));
            return;
        }
        
        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "is going to batch request  " + m.getContent() + " "
          + "at time " + ((PBFT)getProtocol()).getTimestamp()
        );
        
        m.put(PBFTMessage.BATCHSTATEFIELD, PBFT.BATCHSTATE.NOBATCH);

        count++;
        
        int maxBatchSize = ((PBFT)getProtocol()).getMaxBatchSize();

        if(count >= maxBatchSize){
            getProtocol().perform(new BatchRequestAction());
            count = 0;
        }
    }





}
