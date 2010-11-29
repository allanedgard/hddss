/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

/**
 *
 * @author aliriosa
 */
public class Calculator {
    public enum OPERATION{
        PLUS, MINUS, TIMES, DIV, POW, SQR;
    }
    public double plus(double a, double b){
        return a+b;
    }

    public double minus(double a, double b){
        return a-b;
    }

   public double times(double a, double b){
       return a*b;
   }

   public double div(double a, double b){
       return a/b;
   }

   public double pow(double a, double b){
       return Math.pow(a, b);
   }

   public double sqr(double a, double b){
       return Math.pow(a, 1.0/b);
   }
}
