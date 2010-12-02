package br.ufba.lasid.jds.prototyping.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALLAN
 */
public class ChannelOmissionDeterministic extends ChannelOmission {

        Randomize r;
        double prob;
        ChannelDeterministic c;
    
        ChannelOmissionDeterministic (int t, double p) {
            super(p);
            c = new ChannelDeterministic(t);
        }
        
        int delay() {
            return c.delay();
        }           
    
}




