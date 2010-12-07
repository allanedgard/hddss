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
        CalculatorPayload args = (CalculatorPayload)arg;
        CalculatorPayload result = args;
        
        if(args == null){
            result.put(Calculator.RESULT, "NOP");
            return result;
        }
        
        if(args.size() < 3){
            result.put(Calculator.RESULT, "[ERROR]INVALID OPERATION");
            return result;
        }

        Calculator.OPERATION opcode = (Calculator.OPERATION)args.get(Calculator.OPCODE);
        double op1 = ((Double)args.get(Calculator.OP1)).doubleValue();
        double op2 = ((Double)args.get(Calculator.OP2)).doubleValue();
        
        

        if(opcode.equals(Calculator.OPERATION.DIV)){
            result.put(Calculator.RESULT, new Double(calculator.div(op1, op2)));
        }

        if(opcode.equals(Calculator.OPERATION.MINUS)){
            result.put(Calculator.RESULT, new Double(calculator.minus(op1, op2)));
        }

        if(opcode.equals(Calculator.OPERATION.PLUS)){
            result.put(Calculator.RESULT, new Double(calculator.plus(op1, op2)));
        }

        if(opcode.equals(Calculator.OPERATION.POW)){
            result.put(Calculator.RESULT, new Double(calculator.pow(op1, op2)));
        }

        if(opcode.equals(Calculator.OPERATION.SQR)){
            result.put(Calculator.RESULT, new Double(calculator.sqr(op1, op2)));
        }

        if(opcode.equals(Calculator.OPERATION.TIMES)){
            result.put(Calculator.RESULT, new Double(calculator.times(op1, op2)));
        }

        return result;
    }    

}
