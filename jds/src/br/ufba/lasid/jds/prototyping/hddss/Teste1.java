/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.lasid.jds.prototyping.hddss;

/**
 *
 * @author allan
 */
public class Teste1 {
    
   public static void main(String[] args) {
    Randomize x = new br.ufba.lasid.jds.prototyping.hddss.Randomize();
    String x1="a";
            
   
    x.setDistribution("expntl(10.0)");
    System.out.println(x.TYPE);
    System.out.println(x.genericDistribution());
   }
}
