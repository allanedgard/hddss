/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTPrePrepareExecutor extends Executor{

    public PBFTPrePrepareExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getMessage();

        if(checkPrePrepare(m)){
            m.setType(PBFTMessage.TYPE.PREPARE);
            getProtocol().getCommunicator().multicast(
                m, (Group)getProtocol().getContext().get(PBFT.LOCALGROUP)
            );
        }
        
    }

    /**
     * [TODO] this method must check the encriptation of the message and do the
     * procedures specified in Castro and Liskov (1999).
     * @param m
     * @return
     */

    private boolean checkPrePrepare(PBFTMessage m) {
        throw new UnsupportedOperationException("Not yet implemented");
    }



}
