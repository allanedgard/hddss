/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

import br.ufba.lasid.jds.prototyping.hddss.Randomize;
import br.ufba.lasid.jds.prototyping.hddss.pbft.SimulatedPBFTClientAgent;
import br.ufba.lasid.jds.util.IPayload;
import br.ufba.lasid.jds.jbft.pbft.PBFTClient;

/**
 *
 * @author aliriosa
 */
public class CalcPBFTClient extends SimulatedPBFTClientAgent{
    private double rgp = 0.0;
    private Randomize r = new Randomize();
    private volatile boolean waiting = false;
    private static int nThread = 0;


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
        
        if(hasRequest() && !waiting){
                waiting = true;
                new ClientCaller().start();
        }
            
    }

    @Override
    public void receiveResult(IPayload content) {

        CalculatorPayload calc = (CalculatorPayload) content;

        System.out.println(
            "client [p" + getAgentID()+"] has obtained result = " + calc +
            "at time = " + getProtocol().getClock().value()
        );
        
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
        return Math.round(Math.random() * 100)/10.0;
    }

    private double selectOperator2(){
        return Math.round(Math.random() * 100)/10.0;
    }


    /**
     * It adapts to the simulator.
     */
    public void doCall(){
            CalculatorPayload operation = newContent();

            CalculatorPayload calc =
                    (CalculatorPayload)((PBFTClient)getProtocol()).syncCall(operation);

            System.out.println(
                "client [p" + getAgentID()+"] has obtained result = " + calc +
                " at time = " + getProtocol().getClock().value()
            );
            
        
    }

    class ClientCaller extends Thread{

        public ClientCaller() {
            super("ClientCaller" + nThread);
            nThread ++;
        }


        @Override
        public void run() {
            doCall();
            
            waiting = false;

        }
        
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

}
