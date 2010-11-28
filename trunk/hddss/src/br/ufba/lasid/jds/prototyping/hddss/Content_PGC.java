package br.ufba.lasid.jds.prototyping.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class Content_PGC {
    
    int V;
    int M;
    
    Content_PGC(int v, int m) {
        V = v;
        M = m;
    }
    
    public String toString() {
        return "V = " + Integer.toString(V) + " M = " + Integer.toString(M);
     }

}
