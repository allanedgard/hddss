package br.ufba.lasid.jds.prototyping.hddss;

import java.lang.reflect.*;

public class ChannelProbabilistic extends Channel {

    Randomize x;
    int minValue=0;
    
    ChannelProbabilistic () {
        //super(.05);
        x=new Randomize();
    }    
    
    public void setDistribution(String dt) {
        String at = dt.substring(1, dt.length()-1);
        x.setDistribution(at);
    }
    
    public void setMinValue(String dt) {
        minValue = Integer.parseInt(dt);
    }
    int delay() {
        double d;
        d = minValue+ x.genericDistribution();
        //if (d < minValue)  d=minValue;
        return (int) d;
    }
    
    /*
    boolean status() {
        return true;
    }
    */
}