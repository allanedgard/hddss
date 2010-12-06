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

        for(Object item : buffer){
            PBFTMessage batch = (PBFTMessage) item;
            int batchSize = ((Integer)batch.get(PBFTMessage.BATCHSIZEFIELD)).intValue();

            for(int i = 0; i < batchSize; i++){
                String requestField = ((PBFT)getProtocol()).getRequestField(i);
                PBFTMessage req = (PBFTMessage)batch.get(requestField);
                
                boolean executed = false;
                        
                 if(req.get(PBFTMessage.EXECUTEDFIELD)!=null)
                     executed = (Boolean)req.get(PBFTMessage.EXECUTEDFIELD);

                if(!executed){
                    makeSendReply(req);
                }
            }

        }
        /*
        PBFTMessage m = (PBFTMessage)act.getWrapper();
        
        if(!(m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.EXECUTE)))
            return;
        
        int batchSize = ((Integer)m.get(PBFTMessage.BATCHSIZEFIELD)).intValue();

        for(int i = 0; i < batchSize; i++){
            String requestField = ((PBFT)getProtocol()).getRequestField(i);
            PBFTMessage req = (PBFTMessage)m.get(requestField);
            makeSendReply(req);
        }

        //System.out.println(m.get(PBFTMessage.SEQUENCENUMBERFIELD));
        ((PBFT)getProtocol()).setLastCommitedSequenceNumber(
            (Long)m.get(PBFTMessage.SEQUENCENUMBERFIELD)
         );
*/
    }

    public void makeSendReply(PBFTMessage req){
       req.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.SENDREPLY);

       req.setContent(getServer().doService(req.getContent()));
       req.put(PBFTMessage.EXECUTEDFIELD, true);
       System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "executed request " + req.get(PBFTMessage.PAYLOADFIELD) + " "
          + "at time "  + ((PBFT)getProtocol()).getTimestamp()
       );

/*        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTServerExecuteExecutor.execute] call server.reply() for "
          + "execution of request " + req + " with result " + req.getContent()
          + " by server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );
*/
        getProtocol().doAction(req);
        
    }
}
