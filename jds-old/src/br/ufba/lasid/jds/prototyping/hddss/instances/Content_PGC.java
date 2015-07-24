package br.ufba.lasid.jds.prototyping.hddss.instances;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class Content_PGC {
    
    long V;
    int M;
    
    Content_PGC(long v, int m) {
        V = v;
        M = m;
    }
    
    public String toString() {
        return "V = " + Long.toString(V) + " M = " + Integer.toString(M);
     }

}
