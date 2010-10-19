/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALLAN
 */
public class FailStopProcesso extends ProcessFaultModel {

    boolean parado;
    Randomico r;
    double prob;
    
    FailStopProcesso(RuntimeContainer a, double p) {
        super(a);
        r = new Randomico();
        prob = p;
        parado = false;
    }
    
    FailStopProcesso(RuntimeContainer a) {
        super(a);
        r = new Randomico();
        prob = r.uniform();
        parado = false;
    }
    public void avancaTick() {
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
