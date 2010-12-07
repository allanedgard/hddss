/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferCommitAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteReplyPhaseAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveCommitExecutor extends PBFTServerExecutor{

    public PBFTReceiveCommitExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getWrapper();
        PBFTMessage batch = (PBFTMessage)m.get(PBFTMessage.REQUESTFIELD);

        System.out.println(
              "server [p" + getProtocol().getLocalProcess().getID()+"] "
            + "received commit(" + m.get(PBFTMessage.SEQUENCENUMBERFIELD) + ") "
            + "with size " + batch.get(PBFTMessage.BATCHSIZEFIELD) + " "
            + "at time "  + ((PBFT)getProtocol()).getTimestamp()
        );

        if(((PBFT)getProtocol()).isValidSequenceNumber(m)){

            getProtocol().perform(new BufferCommitAction(m));

            if(checkCommit(m)){
                getProtocol().perform(new ExecuteReplyPhaseAction(m));
            }
            return;

        }

        System.out.println(
              "server [p" + getProtocol().getLocalProcess().getID()+"] "
            + "rejected commit(" + m.get(PBFTMessage.SEQUENCENUMBERFIELD) + ") "
            + "because it has a invalid sequence number."
        );

    }

    public boolean isValidCommit(PBFTMessage m){
        if(!isServerAuthenticated(m)){
            return false;
        }

        return (checkDigest(m) && belongsToCurrentView(m) && existsPrepare(m));

    }


    /**
     * this method must check the encriptation of the message and do the
     * procedures specified in Castro and Liskov (1999).
     * @param m
     * @return
     */
    private boolean checkCommit(PBFTMessage m) {

        if (isValidCommit(m)){
            return gotQuorum(m);
        }

        return false;
    }

    private boolean existsPrepare(PBFTMessage m) {
        return ((PBFT)getProtocol()).existsPrepare(m);
    }

    private boolean belongsToCurrentView(PBFTMessage m) {
        return ((PBFT)getProtocol()).belongsToCurrentView(m);
    }

    private boolean gotQuorum(PBFTMessage m) {
        return ((PBFT)getProtocol()).gotQuorum(m);
    }

}
