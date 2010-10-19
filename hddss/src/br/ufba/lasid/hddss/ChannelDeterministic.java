package br.ufba.lasid.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class ChannelDeterministic extends Channel {
    
    int delay;
    
    ChannelDeterministic (int t) {
        delay = t;
    }
    
    int atraso() {
        return delay;
    }
    
    boolean status() {
        return true;
    }
}
