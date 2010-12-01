/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerServerExecuteExecutor;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTServerExecuteExecutor extends ClientServerServerExecuteExecutor{

    public PBFTServerExecuteExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage)act.getMessage();
        
        if(!(m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.EXECUTE)))
            return;
        
        int batchSize = ((Integer)m.get(PBFTMessage.BATCHSIZEFIELD)).intValue();

        for(int i = 0; i < batchSize; i++){
            String requestField = ((PBFT)getProtocol()).getRequestField(i);
            PBFTMessage req = (PBFTMessage)m.get(requestField);
            makeSendReply(req);
        }
    }

    public void makeSendReply(PBFTMessage req){
        req.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.SENDREPLY);
        req.setContent(getServer().doService(req.getContent()));


        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTServerExecuteExecutor.execute] call server.reply() for "
          + "execution of request " + req + " with result " + req.getContent()
          + " by server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );

        getProtocol().doAction(req);
        
    }
}
