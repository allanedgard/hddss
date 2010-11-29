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

        if(opcode == Calculator.OPERATION.TIMES.ordinal()){
            
        }
        return super.doService(arg);
    }    

}
