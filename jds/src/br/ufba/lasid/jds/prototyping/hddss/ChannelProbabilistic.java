package br.ufba.lasid.jds.prototyping.hddss;

import java.lang.reflect.*;

public class ChannelProbabilistic extends Channel {

    Randomize x;
    
    ChannelProbabilistic () {
        x=new Randomize();
    }    
    
    public void setDistribution(String dt) {
        x.setDistribution(dt);
    }

    int delay() {
        return (int) x.genericDistribution();
    }
    
    boolean status() {
        return true;
    }
    
}