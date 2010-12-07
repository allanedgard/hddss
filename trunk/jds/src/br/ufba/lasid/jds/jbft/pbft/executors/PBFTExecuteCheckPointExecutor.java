/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTExecuteCheckPointExecutor extends PBFTServerExecutor{

    public PBFTExecuteCheckPointExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage checkpoint = (PBFTMessage) act.getWrapper();

        Long checkStateSeq = (Long)checkpoint.get(PBFTMessage.SEQUENCENUMBERFIELD);
        Long lastStableSeq = ((PBFT)getProtocol()).getLastStableStateSequenceNumber();

        Buffer cmdBuffer = new Buffer(); 
        cmdBuffer.addAll(((PBFT)getProtocol()).getCommittedBuffer());

        Buffer cmtBuffer = new Buffer();
        cmtBuffer.addAll(((PBFT)getProtocol()).getCommitBuffer());

        Buffer pBuffer = new Buffer();
        pBuffer.addAll(((PBFT)getProtocol()).getPrepareBuffer());

        Buffer ppBuffer = new Buffer();
        ppBuffer.addAll(((PBFT)getProtocol()).getPreprepareBuffer());

        Buffer reqBuffer = new Buffer();
        reqBuffer.addAll(((PBFT)getProtocol()).getRequestBuffer());

        for(Object item: cmdBuffer){
            PBFTMessage m = (PBFTMessage) item;

        }



    }



    
}
