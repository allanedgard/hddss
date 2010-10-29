/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss.bft;

import br.ufba.lasid.hddss.Agent;
import br.ufba.lasid.hddss.Message;

/**
 *
 * @author aliriosa
 */
public class Agent_BFT extends Agent{

    public enum BFTClientActions{
        EXEC, GETRESULTS
    }

    @Override
    public void deliver(Message msg) {
        deliver((BFTMessage)msg);
    }

    public void deliver(BFTMessage msg){
        //do nothing
    }

}
