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
    Teste() {}
    
    
    public int soma() {
        System.out.println("ei");
        return 10;
    }
    
    public void teste() {
        System.out.println("fui");
    }
    
    public static void main(String[] args) {
        try {
            //Network x = (Network) Factory.create("br.ufba.lasid.hddss.MulticastNetwork<br.ufba.lasid.hddss.NetworkDeterministic>",
            //    br.ufba.lasid.jds.prototyping.hddss.Network.class.getName());
            ChannelProbabilistic c = new ChannelProbabilistic();
            //c.setDistribution("erlang(10.0,5.0)");
            c.setDistribution("R(\"rnorm(1000000, mean=10, sd=5)\")");
            System.out.println(c.delay());
            System.out.println(c.delay());
            System.out.println(c.delay());
            System.out.println(c.delay());
            ChannelProbabilistic c1 = new ChannelProbabilistic();
            //c.setDistribution("erlang(10.0,5.0)");
            c1.setDistribution("R(\"rlnorm(1000000, mean=10, sd=5)\")");
            System.out.println(c1.delay());
            System.out.println(c1.delay());
            System.out.println(c1.delay());
            System.out.println(c1.delay());

            Teste x = new Teste();
            EventGenerator e= new EventGenerator();
            e.setProb("0.3");
            e.setAction(x, "teste");
            e.trigger();
            e.trigger();
            e.trigger();
            e.trigger();
            e.trigger();
            e.trigger();
            
            Tracing T1 = new Tracing();
            T1.setFilename("c:/users/allan/desktop/teste.txt");
            System.out.println(T1.getValue());
            System.out.println(T1.getValue());
            System.out.println(T1.getValue());
            System.out.println(T1.getValue());
            System.out.println(T1.getValue());
            System.out.println(T1.getValue());
            System.out.println(T1.getValue());
            System.out.println(T1.getValue());

            IntegrationR.getInstance().end();
        } catch (Exception ex) {
            //Logger.getLogger(Teste.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

}
