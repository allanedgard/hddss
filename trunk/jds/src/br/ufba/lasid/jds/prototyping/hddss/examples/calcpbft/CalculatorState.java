/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IState;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class CalculatorState  implements IState<Object, Long>, Serializable{
    
    private static final long serialVersionUID = -2076144593995036848L;
    long[] variables = new long[Calculator.OPERATION.values().length];

    public CalculatorState() {
        for(int i = 0; i < variables.length; i ++){
            variables[i] = 0L;
        }
    }


    public void set(Object ID, Long value) {
        Calculator.OPERATION opcode = (Calculator.OPERATION) ID;
        variables[opcode.ordinal()] = value;
    }

    public Long get(Object ID) {
        Calculator.OPERATION opcode = (Calculator.OPERATION) ID;
        return variables[opcode.ordinal()];
    }

    @Override
    public String toString() {
        String vars = "";
        String more = "";
        for(int i = 0; i < variables.length ; i++){
            vars = vars + more + Calculator.OPERATION.values()[i].toString() + "=" + variables[i];
            more = ";";
        }
        return "CalculatorState{" + "variables=" + vars + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CalculatorState other = (CalculatorState) obj;
        if (!Arrays.equals(this.variables, other.variables)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Arrays.hashCode(this.variables);
        return hash;
    }

    public IState<Object, Long> copy() {
        CalculatorState calc = new CalculatorState();
        
        System.arraycopy(variables, 0, calc.variables, 0, variables.length);

        return calc;
    }

    
    
}
