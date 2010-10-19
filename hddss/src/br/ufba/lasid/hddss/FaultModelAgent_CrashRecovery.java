package br.ufba.lasid.hddss;
import br.ufba.lasid.hddss.FaultModelAgent;
import br.ufba.lasid.hddss.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALLAN
 */
public class FaultModelAgent_CrashRecovery extends FaultModelAgent {

    boolean parado;
    Randomize r;
    double prob;
    double prob_repair;
    
    FaultModelAgent_CrashRecovery(RuntimeContainer a, double p, double p2) {
        super(a);
        r = new Randomize();
        prob = p;
        parado = false;
        prob_repair = p2;
    }
    
    FaultModelAgent_CrashRecovery(RuntimeContainer a) {
        super(a);
        r = new Randomize();
        prob = r.uniform();
        parado = false;
        prob_repair = r.uniform();
    }
    
    public void avancaTick() {
            if (!parado) {
                if (r.uniform() <= prob) {
                    this.crash();
                }
                else infra.execute();
            }
            else {
                if (r.uniform() <= prob_repair) {
                    this.recovery();
                };                 
            }
        }
    
    private void crash() {
        parado = true;
    }
    
    private void recovery() {
        parado = false;
        infra.execute();
    }

}
