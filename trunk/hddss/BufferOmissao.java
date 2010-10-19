/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class BufferOmissao extends Buffer {

     double prob;
     Randomico r;
    
     BufferOmissao(double p) {
         super();
         prob=p;
         r = new Randomico();
     };
     
     BufferOmissao() {
         super();
         r = new Randomico();
         prob = r.uniform();
     };
     
     
     public synchronized void adiciona(int tempo, Mensagem msg) {
                if (r.uniform() <= prob) {
                    return;
                }
                else super.adiciona(tempo, msg);     
     }
    
}
