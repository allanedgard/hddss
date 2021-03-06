/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.instances.pbft.examples.calcpbft;

import br.ufba.lasid.jds.ft.IRecoverableServer;
//import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IState;
import br.ufba.lasid.jds.management.memory.state.IState;
import br.ufba.lasid.jds.prototyping.hddss.instances.pbft.SimulatedPBFTServerAgent;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class CalcPBFTServer extends SimulatedPBFTServerAgent implements IRecoverableServer<Integer>{//IRecoverableServer<Integer>{

    
    transient public Calculator calculator = new Calculator();
    transient protected int ncalcs = 0;
    transient protected CalculatorState _state = new CalculatorState();

    public String geraDump(int size) {
        String dump = String.format("%1$#"+size+"s", "");
//        for (int i=0; i<size; i++) {
//            dump = dump + " ";
//        }
        return dump;
    }

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

        int resp_size = ((Integer)args.get("response_size")).intValue();

        
        if(r != null){
            result.put(Calculator.RESULT, r);
        }else{
            result.put(Calculator.RESULT,"[ERROR]INVALID OPERATION");
        }

        result.put("dump", geraDump(resp_size));

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
       if(state != null){
        _state = (CalculatorState)state;
        return;
       }

       _state = new CalculatorState();
    }

}
