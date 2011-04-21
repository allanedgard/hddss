/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

import br.ufba.lasid.jds.prototyping.hddss.Randomize;
import br.ufba.lasid.jds.prototyping.hddss.pbft.SimulatedPBFTClientAgent;
import br.ufba.lasid.jds.util.IPayload;
import br.ufba.lasid.jds.jbft.pbft.client.PBFTClient;
import br.ufba.lasid.jds.util.JDSUtility;

/**
 *
 * @author aliriosa
 */
public class CalcPBFTClient extends SimulatedPBFTClientAgent{
    private transient double rgp = 0.0;
    private transient Randomize r = new Randomize();
    private transient int send = 0;
    private transient int recv = 0;


    public void setRequestGenerationProbability(String prob){
        rgp = Double.parseDouble(prob);
    }

    public boolean hasRequest(){
        return (r.uniform() <= rgp);
    }


    public CalcPBFTClient() {

    }

    @Override
    public void execute() {
        super.execute();
        if(hasRequest() && send == recv){
           send++;
           doCall();
        }            
    }

    @Override
    public void receiveResult(IPayload content) {

        CalculatorPayload calc = (CalculatorPayload) content;

        JDSUtility.debug("client [p" + getAgentID()+"] has obtained result = " + calc + " at time " + getProtocol().getClockValue());
         recv++;       
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
    private Calculator.OPERATION selectOperation() {
        int range = Calculator.OPERATION.values().length;
        int opindex = (int) (Math.random()* range);

        return Calculator.OPERATION.values()[opindex];
    }

    private double selectOperator1(){
        return Math.round(Math.random() * 100)/100.0;
    }

    private double selectOperator2(){
        return Math.round(Math.random() * 100)/100.0;
    }


    /**
     * It adapts to the simulator.
     */
    public   void doCall(){
      CalculatorPayload operation = newContent();

      ((PBFTClient)getProtocol()).syncCall(operation);
    }


    @Override
    public void shutdown() {
        super.shutdown();
    }

}
