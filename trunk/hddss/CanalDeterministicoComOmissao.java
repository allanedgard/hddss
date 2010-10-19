/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALLAN
 */
public class CanalDeterministicoComOmissao extends CanalComOmissao {

        Randomico r;
        double prob;
        CanalDeterministico c;
    
        CanalDeterministicoComOmissao (int t, double p) {
            super(p);
            c = new CanalDeterministico(t);
        }
        
        int atraso() {
            return c.atraso();
        }           
    
}




