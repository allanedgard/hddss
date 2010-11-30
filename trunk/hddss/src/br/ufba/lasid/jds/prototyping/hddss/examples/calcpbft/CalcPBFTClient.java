/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft.Calculator.OPERATION;
import br.ufba.lasid.jds.prototyping.hddss.pbft.Agent_ClientPBFT;

/**
 *
 * @author aliriosa
 */
public class CalcPBFTClient extends Agent_ClientPBFT{
    
    @Override
    public void execute() {

        Calculator.OPERATION opcode = selectOperation();

        Double op1 = new Double(selectOperator1());
        Double op2 = new Double(selectOperator2());

        PBFTMessage m = PBFTMessage.newRequest();

        m.put(Calculator.OPCODE, opcode);
        m.put(Calculator.OP1, op1);
        m.put(Calculator.OP2, op2);
        m.put(PBFTMessage.SOURCEFIELD, this);
        m.put(PBFTMessage.DESTINATIONFIELD, getGroup());

        getProtocol().doAction(m);
        
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

    

}
