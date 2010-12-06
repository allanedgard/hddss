/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.executors.ClientServerServerExecuteRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReplyMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTServerExecuteRequestExecutor extends ClientServerServerExecuteRequestExecutor{

    public PBFTServerExecuteRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        Buffer buffer = ((PBFT)getProtocol()).getCommittedBuffer();
        Buffer repBuffer = ((PBFT)getProtocol()).getReplyBuffer();

        for(Object item : buffer){
            PBFTMessage batch = (PBFTMessage) item;
            int batchSize = ((Integer)batch.get(PBFTMessage.BATCHSIZEFIELD)).intValue();

            for(int i = 0; i < batchSize; i++){
                String requestField = ((PBFT)getProtocol()).getRequestField(i);
                PBFTMessage req = (PBFTMessage)batch.get(requestField);
                
                 if(!PBFT.isABufferedMessage(repBuffer, req)){
                    req = makeSendReply(req);
                    ((PBFT)getProtocol()).getReplyBuffer().add(req);
                }
            }

        }
    }

    public PBFTMessage makeSendReply(PBFTMessage req){
       PBFTMessage replay = new PBFTReplyMessage();
       req.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.SENDREPLY);
       
       replay.putAll(req);
       
       replay.setContent(getServer().doService(req.getContent()));
       

       System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "executed request " + replay.get(PBFTMessage.PAYLOADFIELD) + " "
          + "at time "  + ((PBFT)getProtocol()).getTimestamp()
       );

        getProtocol().doAction(replay);

        return replay;
        
    }
}
