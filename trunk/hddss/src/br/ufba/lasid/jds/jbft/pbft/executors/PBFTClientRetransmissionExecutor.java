/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;

/**
 *
 * @author aliriosa
 */
public class PBFTClientRetransmissionExecutor extends Executor{

    public PBFTClientRetransmissionExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        super.execute(act);
    }
    

}
