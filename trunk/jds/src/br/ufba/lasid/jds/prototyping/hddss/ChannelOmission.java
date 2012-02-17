package br.ufba.lasid.jds.prototyping.hddss;

public abstract class ChannelOmission extends Channel {
    
    Randomize r;
    double prob;
    
    ChannelOmission (double p) {
        r = new Randomize();
        prob=p;
    }    




    abstract int delay();
    
    boolean status() {
        if (r.uniform() <= prob) {
                return false;
            }
        else return true;
    } 
    
    
}
