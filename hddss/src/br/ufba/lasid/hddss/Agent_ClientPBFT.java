/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss;

import br.ufba.lasid.util.Communicator;
import br.ufba.lasid.jbft.pbft.PBFT;
import br.ufba.lasid.jbft.pbft.PBFTMessage;
import br.ufba.lasid.jbft.pbft.actions.ReceiveReplyAction;
import br.ufba.lasid.jbft.pbft.actions.SendRequestAction;
import br.ufba.lasid.jbft.pbft.executors.ReceiveReplyExecutor;
import br.ufba.lasid.jbft.pbft.executors.SendRequestExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class Agent_ClientPBFT extends Agent_PBFT{
    
    Randomize r = new Randomize();
    
    @Override
    public void deliver(Message msg) {
        super.deliver(msg);
    }

    @Override
    public void execute() {
        super.execute();
        if (r.uniform() <= prob) {
            System.out.println("[Agent_ClientPBFT] call Agent_ClientPBFT.execute");
            int x = r.irandom(0, 3);

            int op = r.irandom(0, 1);
            
            int v = 0;

            if (op == 1) {
                v = r.irandom(1,10);
            };
            PBFTMessage m = new PBFTMessage(PBFTMessage.TYPE.SENDREQUEST);

            m.add(op + "<" + x + "," + v + ">");
            m.add(new Long(infra.clock.value()));
            m.add(new Integer(id));
            
            pbft.perform(new SendRequestAction(m));
            
            
         }


    }

    @Override
    public void receive(Message msg) {
        super.receive(msg);
    }

    @Override
    public void setup() {
        super.setup();
        pbft.addExecutor(SendRequestAction.class, new SendRequestExecutor(pbft));
        pbft.addExecutor(ReceiveReplyAction.class, new ReceiveReplyExecutor(pbft));
    }

}
