/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class Teste {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //Network x = (Network) Factory.create("br.ufba.lasid.hddss.MulticastNetwork<br.ufba.lasid.hddss.NetworkDeterministic>",
            //    br.ufba.lasid.jds.prototyping.hddss.Network.class.getName());
            ChannelProbabilistic c = new ChannelProbabilistic();
            System.out.println("oi");
            c.setDistribution("R(\"rnorm(1, mean=10, sd=5)\")");
            System.out.println("some delays:");
            System.out.println(c.delay());
            System.out.println(c.delay());
            System.out.println(c.delay());
            System.out.println(c.delay());
            
            // ,22,\"211\"
        } catch (Exception ex) {
            //Logger.getLogger(Teste.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

}
