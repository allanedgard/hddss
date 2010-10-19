package br.ufba.lasid.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALLAN
 */
public class ChannelErlang {
    double media;
    double desvio;
    
    ChannelErlang (double t, double s) {
        media = t;
        desvio = s;
    }
    
    int atraso() {
        Randomize x = new Randomize();
        return (int) (x.erlang(media, desvio));
    }
    
    boolean status() {
        return true;
    }
    
}
