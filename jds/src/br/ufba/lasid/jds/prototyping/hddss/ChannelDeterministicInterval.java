package br.ufba.lasid.jds.prototyping.hddss;

public class ChannelDeterministicInterval extends Channel {
    
    Randomize x;
    int delay;
    int max;
    int min;
    
    ChannelDeterministicInterval (int t) {
        delay = t;
        x=new Randomize();
    }
    
    ChannelDeterministicInterval () {
        x=new Randomize();
    }
    
    int delay() {
        return (int) x.uniform(min, max);
    }
    
    public void setDeltaMaximo(String dt) {
            max = Integer.parseInt(dt);
     }
    
    public void setDeltaMinimo(String dt) {
            min = Integer.parseInt(dt);
     }
    
    public void setDelay(String dt) {
            delay = Integer.parseInt(dt);
     }
    
    boolean status() {
        return true;
    }
}
