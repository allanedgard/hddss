/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeViewExecutor extends Executor{

    public PBFTChangeViewExecutor(Protocol protocol) {
        super(protocol);
    }

    /**
     * [TODO]
     * @param act
     */
    @Override
    public synchronized void execute(Action act) {
            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTChangeViewExecutor.execute]"
             );
    }


    
}
