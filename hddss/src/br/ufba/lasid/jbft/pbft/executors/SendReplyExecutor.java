/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft.executors;

import br.ufba.lasid.hddss.Agent_ClientPBFT;
import br.ufba.lasid.util.Executor;
import br.ufba.lasid.util.Process;
import br.ufba.lasid.util.Protocol;
import br.ufba.lasid.util.actions.Action;
import br.ufba.lasid.jbft.pbft.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class SendReplyExecutor extends Executor{

    public SendReplyExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public void execute(Action act) {
        System.out.println("[SendReplyExecutor] call SendReplyExecutor.execute");
        PBFTMessage m = (PBFTMessage) act.getMessage();

        
        m.setType(PBFTMessage.TYPE.RECEIVEREPLY);


        int cid   = Integer.parseInt((String)m.get(2));

        Agent_ClientPBFT agent = new Agent_ClientPBFT();
        agent.id = cid;
        
        protocol.getCommunicator().unicast(m, (Process) agent);
        
    }


}
