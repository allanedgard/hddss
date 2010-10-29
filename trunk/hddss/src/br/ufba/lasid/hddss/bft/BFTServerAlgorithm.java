/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss.bft;

import br.ufba.lasid.hddss.Algorithm;

/**
 *
 * @author aliriosa
 */
public class BFTServerAlgorithm implements Algorithm<Object>{

    public enum PHASE{
        PRE_PREPARE, PREPARE, COMMIT, REPLY
    }
    
    public Object getOutput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setInput(Object input) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getLatency() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(Object ... args) {
        
    }

}
