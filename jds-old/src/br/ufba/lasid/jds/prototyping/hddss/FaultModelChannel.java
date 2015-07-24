/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.lasid.jds.prototyping.hddss;

/**
 *
 * @author Allan
 */
public class FaultModelChannel extends ChannelOmission {
    
    Randomize x;
    int minValue=0;
    
    FaultModelChannel () {
        super(.05);
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
    
    
}
