/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

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

        System.out.println(
            "before checkpoint, server [p" + getProtocol().getLocalProcess().getID() + "] "
          + "has request buffer size " +  ((PBFT)getProtocol()).getRequestBuffer().size() + " "
          + "preprepare buffer size " + ((PBFT)getProtocol()).getPrePrepareBuffer().size() + " "
          + "prepare buffer size " + ((PBFT)getProtocol()).getPrepareBuffer().size() + " "
          + "commit buffer size " + ((PBFT)getProtocol()).getCommitBuffer().size() + " "
          + "committed buffer size " + ((PBFT)getProtocol()).getCommittedBuffer().size() + " "
          + "reply buffer size " + ((PBFT)getProtocol()).getReplyBuffer().size() + " "
        );

        ((PBFT)getProtocol()).garbage(checkStateSeq);
        ((PBFT)getProtocol()).setLastCheckpoint(checkpoint);

        System.out.println(
            "after checkpoint, server [p" + getProtocol().getLocalProcess().getID() + "] "
          + "has request buffer size " +  ((PBFT)getProtocol()).getRequestBuffer().size() + " "
          + "preprepare buffer size " + ((PBFT)getProtocol()).getPrePrepareBuffer().size() + " "
          + "prepare buffer size " + ((PBFT)getProtocol()).getPrepareBuffer().size() + " "
          + "commit buffer size " + ((PBFT)getProtocol()).getCommitBuffer().size() + " "
          + "committed buffer size " + ((PBFT)getProtocol()).getCommittedBuffer().size() + " "
          + "reply buffer size " + ((PBFT)getProtocol()).getReplyBuffer().size() + " "
        );

    }



    
}
