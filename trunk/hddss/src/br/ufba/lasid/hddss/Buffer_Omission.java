package br.ufba.lasid.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class Buffer_Omission extends Buffer {

     double prob;
     Randomize r;
    
     Buffer_Omission(double p) {
         super();
         prob=p;
         r = new Randomize();
     };
     
     Buffer_Omission() {
         super();
         r = new Randomize();
         prob = r.uniform();
     };
     
     
     public synchronized void adiciona(int tempo, Message msg) {
                if (r.uniform() <= prob) {
                    return;
                }
                else super.adiciona(tempo, msg);     
     }
    
}
