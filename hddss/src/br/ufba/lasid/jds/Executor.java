/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

/**
 *
 * @author aliriosa
 */
public class Executor {
    protected Protocol protocol;

    public Executor(Protocol protocol){
        this.protocol = protocol;
    }
    
    public synchronized void execute(Action act) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
