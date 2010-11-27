/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft.executors;

import br.ufba.lasid.jbft.Executor;
import br.ufba.lasid.jbft.Protocol;
import br.ufba.lasid.jbft.actions.Action;
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
        
        PBFTMessage m = (PBFTMessage)act.getMessage();
       
        String tuple = (String) m.get(0);
        int ts    = Integer.parseInt((String)m.get(1));
        int cid   = Integer.parseInt((String)m.get(2));

        tuple = tuple.replace(">", "");
        
        int opcode = Integer.parseInt(tuple.split("<")[0]);

        String parameters = tuple.split("<")[1];
        
        int x = Integer.parseInt(parameters.split(",")[0]);
        int v = Integer.parseInt(parameters.split(",")[1]);

        //do something
        
        m.setType(PBFTMessage.TYPE.SENDREPLY);
        
        protocol.doAction(m);
    }


}
