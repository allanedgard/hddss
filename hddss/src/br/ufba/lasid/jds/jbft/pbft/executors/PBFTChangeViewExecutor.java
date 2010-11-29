/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.Task;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeViewExecutor extends Executor implements Task{

    public PBFTChangeViewExecutor(Protocol protocol) {
        super(protocol);
    }

    public void runMe() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
