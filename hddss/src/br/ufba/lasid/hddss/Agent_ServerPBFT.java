/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss;

import br.ufba.lasid.jbft.pbft.PBFT;
import br.ufba.lasid.jbft.pbft.PBFTMessage;
import br.ufba.lasid.jbft.pbft.actions.CommitAction;
import br.ufba.lasid.jbft.pbft.actions.ExecuteAction;
import br.ufba.lasid.jbft.pbft.actions.PrePrepareAction;
import br.ufba.lasid.jbft.pbft.actions.PrepareAction;
import br.ufba.lasid.jbft.pbft.actions.ReceiveRequestAction;
import br.ufba.lasid.jbft.pbft.actions.SendReplyAction;
import br.ufba.lasid.jbft.pbft.executors.CommitExecutor;
import br.ufba.lasid.jbft.pbft.executors.ExecuteExecutor;
import br.ufba.lasid.jbft.pbft.executors.PrePrepareExecutor;
import br.ufba.lasid.jbft.pbft.executors.PrepareExecutor;
import br.ufba.lasid.jbft.pbft.executors.ReceiveReplyExecutor;
import br.ufba.lasid.jbft.pbft.executors.SendReplyExecutor;

/**
 *
 * @author aliriosa
 */
public class Agent_ServerPBFT extends Agent_PBFT{    
    @Override
    public void deliver(Message msg) {
        super.deliver(msg);
    }

    @Override
    public void execute() {
        super.execute();
    }

    @Override
    public void receive(Message msg) {
        PBFTMessage m = (PBFTMessage)msg.content;        
        pbft.doAction(m);
    }

    @Override
    public void setup() {
        super.setup();        
        pbft.addExecutor(ReceiveRequestAction.class, new ReceiveReplyExecutor(pbft));
        pbft.addExecutor(PrePrepareAction.class, new PrePrepareExecutor(pbft));
        pbft.addExecutor(PrepareAction.class, new PrepareExecutor(pbft));
        pbft.addExecutor(CommitAction.class, new CommitExecutor(pbft));
        pbft.addExecutor(ExecuteAction.class, new ExecuteExecutor(pbft));
        pbft.addExecutor(SendReplyAction.class, new SendReplyExecutor(pbft));
    }

}
