package br.ufba.lasid.jds.prototyping.hddss;

public class ChannelDeterministicInterval extends Channel {
    
    int delay_min;
    int delay_max;
    Randomize r;
    
    ChannelDeterministicInterval () {
        r = new Randomize ();
    }

     public void setDeltaMaximo(String dt) {
            delay_max = Integer.parseInt(dt);
     }

     public void setDeltaMinimo(String dt) {
            delay_min = Integer.parseInt(dt);
     }

    
    int delay() {
        return r.irandom(delay_min, delay_max);
    }
    
    boolean status() {
        return true;
    }
}
