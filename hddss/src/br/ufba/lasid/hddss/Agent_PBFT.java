/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss;

import br.ufba.lasid.jbft.Communicator;
import br.ufba.lasid.jbft.Message;
import br.ufba.lasid.jbft.Process;
import br.ufba.lasid.jbft.pbft.PBFT;
import br.ufba.lasid.jbft.pbft.PBFTMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class Agent_PBFT extends Agent implements Communicator{

    PBFT pbft = new PBFT();
    Communicator comm;

    @Override
    public void setup() {
        super.setup();        
    }

    @Override
    public void receive(br.ufba.lasid.hddss.Message msg) {
        PBFTMessage m = (PBFTMessage) msg.content;
        pbft.doAction(m);

    }



   public void setCommunicator (String cm) {
        try {
            comm = (Communicator) Factory.create(cm, cm);
            pbft.setCommunicator(comm);
        } catch (Exception ex) {
            Logger.getLogger(Agent_ClientPBFT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void multicast(Message m, Process group) {
        pbft.getCommunicator().multicast(m, group);
    }

    public void unicast(Message m, Process client) {
        pbft.getCommunicator().unicast(m, client);
    }

}
