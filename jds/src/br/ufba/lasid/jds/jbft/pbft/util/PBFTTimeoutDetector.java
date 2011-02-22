/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.util.ITask;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTTimeoutDetector
        extends Hashtable<String, Object>
        implements ITask
{

    public void runMe() {

        onTimeout();
        
    }

    public abstract void onTimeout();
    
    public void cancel(){
        //do nothing
    }

}
