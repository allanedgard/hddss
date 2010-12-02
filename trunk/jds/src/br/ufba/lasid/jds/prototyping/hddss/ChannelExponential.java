package br.ufba.lasid.jds.prototyping.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */

public class ChannelExponential extends Channel {
    
    double mean;
    
    ChannelExponential (double t) {
        mean = t;
    }

    ChannelExponential () {
    }    
    
    public void setMean(String dt) {
            mean = Float.parseFloat(dt);
    }
    
    int delay() {
        Randomize x = new Randomize();
        return (int) (x.expntl(mean)+mean);
    }
    
    boolean status() {
        return true;
    }
    
}