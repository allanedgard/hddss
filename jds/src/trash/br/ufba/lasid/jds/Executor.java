/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds;

import br.ufba.lasid.jds.util.ITask;

/**
 *
 * @author aliriosa
 */
public class Executor implements ITask{
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
