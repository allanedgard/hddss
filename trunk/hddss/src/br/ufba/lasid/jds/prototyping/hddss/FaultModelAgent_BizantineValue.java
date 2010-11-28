package br.ufba.lasid.jds.prototyping.hddss;
import br.ufba.lasid.jds.prototyping.hddss.FaultModelAgent;
import br.ufba.lasid.jds.prototyping.hddss.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class FaultModelAgent_BizantineValue extends FaultModelAgent {

    Randomize r;
    double prob;
    
    FaultModelAgent_BizantineValue(RuntimeContainer a, double p) {
        super(a);
        r = new Randomize();
        prob = p;
    }
    
    FaultModelAgent_BizantineValue(RuntimeContainer a) {
        super(a);
        r = new Randomize();
        prob = r.uniform();
    }   

    public void sendMessage(int c, Message msg) {
        if (r.uniform() <= prob) {
                    super.sendMessage(c, msg);
                }
        else {
            msg = new Message(msg.sender, msg.destination, (int) r.expntl((double) msg.type), (int) r.expntl((double) msg.logicalClock), msg.physicalClock, msg.content);
            super.sendMessage(c, msg);
            
        }
    }    
    
}
