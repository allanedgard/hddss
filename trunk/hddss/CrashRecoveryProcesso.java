/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALLAN
 */
public class CrashRecoveryProcesso extends ProcessFaultModel {

    boolean parado;
    Randomico r;
    double prob;
    double prob_repair;
    
    CrashRecoveryProcesso(RuntimeContainer a, double p, double p2) {
        super(a);
        r = new Randomico();
        prob = p;
        parado = false;
        prob_repair = p2;
    }
    
    CrashRecoveryProcesso(RuntimeContainer a) {
        super(a);
        r = new Randomico();
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
