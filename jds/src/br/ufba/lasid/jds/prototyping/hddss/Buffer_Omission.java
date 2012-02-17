package br.ufba.lasid.jds.prototyping.hddss;

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
     
     
     public synchronized void add(int tempo, Message msg) {
                if (r.uniform() <= prob) {
                    return;
                }
                else super.add(tempo, msg);
     }
    
}
