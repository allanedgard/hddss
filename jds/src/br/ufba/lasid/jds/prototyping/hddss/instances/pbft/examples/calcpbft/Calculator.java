/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.instances.pbft.examples.calcpbft;

import java.io.Serializable;

/**
 *
 * @author aliriosa
 */
public class Calculator implements Serializable{
    public static String OPCODE = "opcode";
    public static String OP1 = "op1";
    public static String OP2 = "op2";
    public static String RESULT = "result";

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

   public Double solve(OPERATION opcode, double op1, double op2){

        if(opcode.equals(Calculator.OPERATION.DIV)){
            return div(op1, op2);
        }

        if(opcode.equals(Calculator.OPERATION.MINUS)){
            return  minus(op1, op2);
        }

        if(opcode.equals(Calculator.OPERATION.PLUS)){
            return  plus(op1, op2);
        }

        if(opcode.equals(Calculator.OPERATION.POW)){
            return  pow(op1, op2);
        }

        if(opcode.equals(Calculator.OPERATION.SQR)){
            return  sqr(op1, op2);
        }

        if(opcode.equals(Calculator.OPERATION.TIMES)){
            return  times(op1, op2);
        }

        return null;
       
   }
}
