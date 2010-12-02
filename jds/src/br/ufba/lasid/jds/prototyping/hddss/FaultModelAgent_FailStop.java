package br.ufba.lasid.jds.prototyping.hddss;
import br.ufba.lasid.jds.prototyping.hddss.FaultModelAgent;
import br.ufba.lasid.jds.prototyping.hddss.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALLAN
 */
public class FaultModelAgent_FailStop extends FaultModelAgent {

    boolean parado;
    Randomize r;
    double prob;
    
    FaultModelAgent_FailStop(RuntimeContainer a, double p) {
        super(a);
        r = new Randomize();
        prob = p;
        parado = false;
    }
    
    FaultModelAgent_FailStop(RuntimeContainer a) {
        super(a);
        r = new Randomize();
        prob = r.uniform();
        parado = false;
    }
    public void increaseTick() {
            if (!parado)
                infra.agent.done = true;
                if (r.uniform() <= prob) {
                    parado = true;
                }
                else infra.execute();
        }
    
    public boolean status() {
        return !parado;
    }
    
}
