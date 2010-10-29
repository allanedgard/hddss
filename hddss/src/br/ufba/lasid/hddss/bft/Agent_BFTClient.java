/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss.bft;

import br.ufba.lasid.hddss.Randomize;

/**
 *
 * @author aliriosa
 */
public class Agent_BFTClient extends Agent_BFT{
    private Randomize random;
    private double probability = 0.0;
    private BFTClientAlgorithm algorithm = new BFTClientAlgorithm();


    
    @Override
    public void execute() {       
        
        if(random.uniform() >= probability){
            algorithm.execute(BFTClientActions.EXEC, this , getOperation());
        }
        
    }

    @Override
    public void deliver(BFTMessage msg) {
        algorithm.execute(BFTClientActions.GETRESULTS, this, msg);
    }

    
    public long getOperation(){
        //afterwards, we have to improve this method to sort a differents
        //operations as soon we setup in config and as soon we've defined a
        //random function
        return 1;
    }


}
