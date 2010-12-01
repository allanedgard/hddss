/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
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

            Object content = newContent();
            PBFTMessage m = PBFTMessage.newRequest();

            m.setContent(content);
            m.put(PBFTMessage.SOURCEFIELD, this);
            m.put(PBFTMessage.DESTINATIONFIELD, getGroup());

            getProtocol().doAction(m);
            
        }
    }

    @Override
    public void receiveReply(Object content) {
        ((PBFT)getProtocol()).getDebugger().debug(
            "[CalcPBFTClient.receiveReply] result (" + content + ") "
          + " by client(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );
        
    }



    private CalculatorApplicationPayload newContent(){

        CalculatorApplicationPayload content = new CalculatorApplicationPayload();
            
            Calculator.OPERATION opcode = selectOperation();

            Double op1 = new Double(selectOperator1());
            Double op2 = new Double(selectOperator2());

            content.put(Calculator.OPCODE, opcode);
            content.put(Calculator.OP1, op1);
            content.put(Calculator.OP2, op2);

            return content;
        
    }
    private OPERATION selectOperation() {
        return Calculator.OPERATION.PLUS;
    }

    private double selectOperator1(){
        return 1.0;
    }

    private double selectOperator2(){
        return 2.0;
    }

    public void setRequestGenerationProbability(String prob){
        rgp = Double.parseDouble(prob);
    }

    public boolean hasRequest(){
        return (r.uniform() <= rgp);
    }
}
