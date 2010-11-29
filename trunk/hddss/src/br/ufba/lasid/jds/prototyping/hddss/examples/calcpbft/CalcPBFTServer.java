/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

import br.ufba.lasid.jds.prototyping.hddss.pbft.Agent_ServerPBFT;

/**
 *
 * @author aliriosa
 */
public class CalcPBFTServer extends Agent_ServerPBFT{
    public Calculator calculator = new Calculator();

    @Override
    public Object doService(Object arg) {
        Object[] args = (Object[])arg;
        if(args == null){
            System.out.println("nothing to do!");
            return null;
        }
        if(args.length < 3){
            System.out.println("wrong number of parameters");
        }

        int opcode = Integer.parseInt((String)args[0]);
        double op1 = Double.parseDouble((String)args[1]);
        double op2 = Double.parseDouble((String)args[2]);
        Double result = null;

        if(opcode == Calculator.OPERATION.DIV.ordinal()){
            result = new Double(calculator.div(op1, op2));
        }

        if(opcode == Calculator.OPERATION.MINUS.ordinal()){
            result = new Double(calculator.minus(op1, op2));
        }

        if(opcode == Calculator.OPERATION.PLUS.ordinal()){
            result = new Double(calculator.plus(op1, op2));
        }

        if(opcode == Calculator.OPERATION.POW.ordinal()){
            result = new Double(calculator.pow(op1, op2));
        }

        if(opcode == Calculator.OPERATION.SQR.ordinal()){
            result = new Double(calculator.sqr(op1, op2));
        }

        if(opcode == Calculator.OPERATION.TIMES.ordinal()){
            result = new Double(calculator.times(op1, op2));
        }




        return result;
    }    

}
