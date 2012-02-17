package br.ufba.lasid.jds.prototyping.hddss;

public class ChannelDeterministic extends Channel {
    
    int delay;
    
    ChannelDeterministic (int t) {
        delay = t;
    }
    
    ChannelDeterministic () {

    }
    
    int delay() {
        return delay;
    }
    
    public void setDelay(String dt) {
            delay = Integer.parseInt(dt);
     }
    
    boolean status() {
        return true;
    }
}
