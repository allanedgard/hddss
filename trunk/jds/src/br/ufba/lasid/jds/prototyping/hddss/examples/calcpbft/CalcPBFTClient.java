/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.prototyping.hddss.Randomize;
import br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft.Calculator.OPERATION;
import br.ufba.lasid.jds.prototyping.hddss.pbft.Agent_ClientPBFT;

/**
 *
 * @author aliriosa
 */
public class CalcPBFTClient extends Agent_ClientPBFT{
    private double rgp = 0.0;
    private Randomize r = new Randomize();
    
    @Override
    public void execute() {

        if(hasRequest()){

            CalculatorPayload operation = newContent();
            getProtocol().doAction(operation);
            
        }
    }

    @Override
    public void receiveReply(Object content) {
        CalculatorPayload calc = (CalculatorPayload) content;
        System.out.println(
            "client [p" + getProtocol().getLocalProcess().getID()+"] "
          + calc.get(Calculator.OPCODE) + "(" + calc.get(Calculator.OP1)
          + ", " + calc.get(Calculator.OP2) + ") = " + calc.get(Calculator.RESULT)
        );

/*        ((PBFT)getProtocol()).getDebugger().debug(
            "[CalcPBFTClient.receiveReply] result (" + content + ") "
          + " by client(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );
 * 
 */
        
    }



    private CalculatorPayload newContent(){

        CalculatorPayload content = new CalculatorPayload();
            
            Calculator.OPERATION opcode = selectOperation();

            Double op1 = new Double(selectOperator1());
            Double op2 = new Double(selectOperator2());

            content.put(Calculator.OPCODE, opcode);
            content.put(Calculator.OP1, op1);
            content.put(Calculator.OP2, op2);

            return content;
        
    }
    private OPERATION selectOperation() {
        int range = Calculator.OPERATION.values().length;
        int opindex = (int) (Math.random()* range);

        return Calculator.OPERATION.values()[opindex];
    }

    private double selectOperator1(){
        return Math.round(Math.random() * 100)/10.0;
    }

    private double selectOperator2(){
        return Math.round(Math.random() * 100)/10.0;
    }

    public void setRequestGenerationProbability(String prob){
        rgp = Double.parseDouble(prob);
    }

    public boolean hasRequest(){
        return (r.uniform() <= rgp);
    }
}
