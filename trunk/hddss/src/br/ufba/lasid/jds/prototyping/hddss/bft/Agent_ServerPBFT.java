/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.bft;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.prototyping.hddss.Message;

/**
 *
 * @author aliriosa
 */
public class Agent_ServerPBFT extends Agent_PBFT{    
    public int x[];

    @Override
    public void receive(Message msg) {
        PBFTMessage m = (PBFTMessage)msg.getContent();
        pbft.doAction(m);
    }

    @Override
    public void setup() {
        super.setup();        
//        pbft.addExecutor(ReceiveRequestAction.class, new ReceiveRequestExecutor(pbft));
//        pbft.addExecutor(PrePrepareAction.class, new PrePrepareExecutor(pbft));
//        pbft.addExecutor(PrepareAction.class, new PrepareExecutor(pbft));
//        pbft.addExecutor(CommitAction.class, new CommitExecutor(pbft));
//        pbft.addExecutor(ExecuteAction.class, new ExecuteExecutor(pbft));
//        pbft.addExecutor(SendReplyAction.class, new SendReplyExecutor(pbft));
//        x = new int[10];
    }

}
