/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

import br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft.Calculator.OPERATION;
import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IState;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class CalculatorState extends Hashtable<Object, Long> implements IState<Object, Long>{
    
    private static final long serialVersionUID = -2076144593995036848L;

    public void set(Object ID, Long value) {
        put(ID, value);
    }

}
