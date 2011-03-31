/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

import br.ufba.lasid.jds.ft.IRecoverableServer;
//import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IState;
import br.ufba.lasid.jds.management.memory.state.IState;
import br.ufba.lasid.jds.prototyping.hddss.pbft.SimulatedPBFTServerAgent;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class CalcPBFTServer extends SimulatedPBFTServerAgent implements IRecoverableServer<Integer>{//IRecoverableServer<Integer>{

    
    public Calculator calculator = new Calculator();
    protected int ncalcs = 0;
    protected CalculatorState _state = new CalculatorState();

    @Override
    public IPayload executeCommand(IPayload arg) {
        CalculatorPayload args = (CalculatorPayload)arg;
        CalculatorPayload result = new CalculatorPayload();
        
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
        Double r = round(calculator.solve(opcode, op1, op2));
        
        if(r != null){
            result.put(Calculator.RESULT, r);
        }else{
            result.put(Calculator.RESULT,"[ERROR]INVALID OPERATION");
        }

        Long count = _state.get(opcode);
        if(count == null){
            count = 0L;
        }

        count++;

        _state.set(opcode, count);


        //_state.put("ncalcs", ncalcs);

        return result;
    }

    public double round(double value){
        return Math.round(value * 100)/100.0;
    }
    public IState getCurrentState() {
        return _state;
    }

    public void setCurrentState(IState state) {
        _state = (CalculatorState)state;
    }

}
