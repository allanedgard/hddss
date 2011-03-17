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
    private  boolean cansend = false;
    private ClientCaller caller = null;


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
        if(hasRequest()){
            if(caller == null){
                caller = new ClientCaller();
                caller.setName("Caller[" + this.ID +"]");
                caller.start();
            }
            synchronized(caller.lock){
                cansend = true;
            }
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
        public final Object lock = this;

        @Override
        public void run() {
            while(true){
                if(cansend) doCall();
                synchronized(lock){
                    cansend = true;
                    notify();
                }
            }
            

        }
        
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

}
