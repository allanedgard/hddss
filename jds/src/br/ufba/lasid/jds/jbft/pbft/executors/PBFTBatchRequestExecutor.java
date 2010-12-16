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
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTBatchRequestExecutor extends PBFTServerExecutor{

    Buffer incomingRequestBuffer = new Buffer();
    
    public PBFTBatchRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        int batchCount = 0;
        
        Buffer buffer = ((PBFT)getProtocol()).getRequestBuffer();

        PBFTMessage batch = new PBFTBatchMessage();

        /*count the number of no batched requests */
        
        for(Object item : buffer){
            
            PBFTMessage request = (PBFTMessage) item;

            if(PBFT.BATCHSTATE.NOBATCH.equals(request.get(PBFTMessage.BATCHSTATEFIELD))){
                
                String requestField = ((PBFT)getProtocol()).getRequestField(batchCount);

                batch.put(requestField, request);
                
                batchCount ++;
                
            }
        }

        /* if there is request to batch then batching request */
        if(batchCount > 0){

            int maxBatchSize = ((PBFT)getProtocol()).getMaxBatchSize();
            int batchSize = batchCount > maxBatchSize ? maxBatchSize : batchCount;
            
            for(int i = 0; i < batchSize; i++){

                String requestField = ((PBFT)getProtocol()).getRequestField(i);

                PBFTMessage request = (PBFTMessage)batch.get(requestField);

                request.put(PBFTMessage.BATCHSTATEFIELD, PBFT.BATCHSTATE.INBATCH);

            }

            batch.put(PBFTMessage.BATCHSIZEFIELD, new Integer(batchSize));
            batch.put(PBFTMessage.TIMESTAMPFIELD, ((PBFT)getProtocol()).getTimestamp());
            batch.put(PBFTMessage.CLIENTFIELD, getProtocol().getLocalProcess());
            batch.put(PBFTMessage.VIEWFIELD, ((PBFT)getProtocol()).getCurrentView());

            batch = makeDisgest(batch);
            batch = encrypt(batch);

           System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "created a new batch with size " + batchSize + " at time "
              + ((PBFT)getProtocol()).getTimestamp()
           );

            /* perform phase three (preprepare) */
            getProtocol().perform(new ExecuteStartNewRoundPhaseThreeAction(batch));

            /* if there is more requests to batch then re-perform batch*/
            if(batchCount > maxBatchSize){
                getProtocol().perform(new SendPrePrepareAction(batch));
            }            
        }

    }
    

}
