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
    protected DistributedProtocol protocol;

    public Executor(DistributedProtocol protocol){
        this.protocol = protocol;
    }
    
    public synchronized void execute(Action act) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DistributedProtocol getProtocol() {
        return protocol;
    }


    public void runMe() {
        
    }

}
