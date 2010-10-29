/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss.bft;

import br.ufba.lasid.hddss.Agent;

/**
 *
 * @author aliriosa
 */
public class BFTClientAlgorithm extends BFTAlgorithm{
    Object data = null;
    
    public Object getOutput() {
        return data;
    }

    public void setInput(Object input) {
        data = input;
    }

    public long getLatency() {
        return -1;
    }

    public void execute(Object ... args) {

        Agent_BFT.BFTClientActions action =
                (Agent_BFT.BFTClientActions)args[0];

        if(action.equals(Agent_BFT.BFTClientActions.EXEC)){
           performRequest(args);
        }

        if(action.equals(Agent_BFT.BFTClientActions.GETRESULTS)){
           getReply(args);
        }


    }

    public void performRequest(Object ... args){
        
        Agent client = (Agent)args[1];
        
        if(args.length < 3){
            client.infra.debug("["+BFTClientAlgorithm.class.getName() + "]"
                    +   "bad number of parameters. "
                    +   "Use: clientBFTAlgorithm.execute(AgentID, BFTClientAction, OperationID)"
            );
        }

        
        int id = client.id;
        long op = (Long)args[2];
        long t  = client.infra.clock.value();

        Object[] payload = {BFTMessage.TYPE.REQUEST, op, t, id};

        BFTMessage m = BFTMessage.newRequest(id, -1, payload);

        encripty.setInput(m);
        
        encripty.execute();

        m = (BFTMessage) encripty.getOutput();

        client.send(m);
        
        return;
        
    }

    public void getReply(Object ... args){
        //it hasn't been implemented yet.
    }
}
