/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTCommitExecutor extends Executor{

    public PBFTCommitExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
       PBFTMessage m = (PBFTMessage) act.getMessage();

       if(checkCommit(m)){
           m.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.EXECUTE);
           //m.setType(PBFTMessage.TYPE.EXECUTE);
           getProtocol().doAction(m);
       }
    }
    /**
     * [TODO] this method must check the encriptation of the message and do the
     * procedures specified in Castro and Liskov (1999).
     * @param m
     * @return
     */
    private boolean checkCommit(PBFTMessage m) {
        getProtocol().getContext().put(null, m);
        if ( belongsToCurrentView(m) )
           if ( existsPrepare(m) )
                if ( gotQuorum(m) )
                    return true;
        return false;
    }

        /**
         * [TODO] this method verifies if there was a PrePrepare previously
         * to this message
         */
    private boolean existsPrepare(PBFTMessage m) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

        /**
         * [TODO] this method verifies if there was a PrePrepare previously
         * to this message
         */
    private boolean belongsToCurrentView(PBFTMessage m) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


        /**
         * [TODO] this method verifies if there is at least 2F+1 COMMIT Messages
         */
    private boolean gotQuorum(PBFTMessage m) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

        /**
         * [TODO] this method verifies if the Prepare Messages were processed
         */
    private boolean processedPrepare(PBFTMessage m) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
