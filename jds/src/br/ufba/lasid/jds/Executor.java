/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

import br.ufba.lasid.jds.util.Task;

/**
 *
 * @author aliriosa
 */
public class Executor implements Task{
    protected Protocol protocol;

    public Executor(Protocol protocol){
        this.protocol = protocol;
    }
    
    public synchronized void execute(Action act) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Protocol getProtocol() {
        return protocol;
    }


    public void runMe() {
        
    }

}
