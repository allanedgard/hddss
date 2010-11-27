/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft.executors;

import br.ufba.lasid.hddss.Agent_ServerPBFT;
import br.ufba.lasid.util.Executor;
import br.ufba.lasid.util.Protocol;
import br.ufba.lasid.util.actions.Action;
import br.ufba.lasid.jbft.pbft.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class ExecuteExecutor extends Executor{

    public ExecuteExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public void execute(Action act) {
        System.out.println("[ExecuteExecutor] call ExecuteExecutor.execute");
        PBFTMessage m = (PBFTMessage)act.getMessage();
        System.out.println("[ExecuteExecutor] executing operation " + m);
        String tuple = (String) m.get(0);
        System.out.println("[ExecuteExecutor] geting tuple" + m.get(1));
        int ts    = Integer.parseInt((String)m.get(1));
        System.out.println("[ExecuteExecutor] executing operation " + m.get(2));
        int cid   = Integer.parseInt((String)m.get(2));

        tuple = tuple.replace(">", "");
        
        int opcode = Integer.parseInt(tuple.split("<")[0]);

        String parameters = tuple.split("<")[1];
        
        int x = Integer.parseInt(parameters.split(",")[0]);
        int v = Integer.parseInt(parameters.split(",")[1]);

        //do something
        if (opcode ==1) {
            ( (Agent_ServerPBFT) (this.protocol.getProcess()) ).x[x] = v;

        } else
            v = ( (Agent_ServerPBFT) (this.protocol.getProcess()) ).x[x];

        m.add(((Agent_ServerPBFT) (this.protocol.getProcess()) ).x[x]);

        System.out.println("[SERVER][EXECUTE]" + m);
        
        m.setType(PBFTMessage.TYPE.SENDREPLY);
        
        protocol.doAction(m);
    }


}
