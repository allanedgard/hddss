package br.ufba.lasid.jds.prototyping.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALLAN
 */
public class ChannelErlang {
    double mean;
    double std;
    
    ChannelErlang (double t, double s) {
        mean = t;
        std = s;
    }
    
    int atraso() {
        Randomize x = new Randomize();
        return (int) (x.erlang(mean, std));
    }
    
    boolean status() {
        return true;
    }
    
}
