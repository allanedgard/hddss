/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferPrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCurrentRoundPhaseThreeAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTReceivePrepareExecutor extends PBFTServerExecutor{

    public PBFTReceivePrepareExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getWrapper();
        PBFTMessage batch = (PBFTMessage)m.get(PBFTMessage.REQUESTFIELD);

        System.out.println(
              "server [p" + getProtocol().getLocalProcess().getID()+"] "
            + "received prepare(" + m.get(PBFTMessage.SEQUENCENUMBERFIELD) + ") "
            + "with size " + batch.get(PBFTMessage.BATCHSIZEFIELD) + " "
            + "at time "  + ((PBFT)getProtocol()).getTimestamp()
        );

        if(PBFT.isValidSequenceNumber(m)){
           
            getProtocol().perform(new BufferPrepareAction(m));

            if(checkPrepare(m)){
                getProtocol().perform(new ExecuteCurrentRoundPhaseThreeAction(m));
            }
            return;

        }

        System.out.println(
              "server [p" + getProtocol().getLocalProcess().getID()+"] "
            + "rejected prepare(" + m.get(PBFTMessage.SEQUENCENUMBERFIELD) + ") "
            + "because it has a invalid sequence number."
        );

    }

    public boolean isValidPrepare(PBFTMessage m){
        if(!isServerAuthenticated(m)){
            return false;
        }

        return (checkDigest(m) && belongsToCurrentView(m) && existsPrePrepare(m));

    }


    /**
     * this method must check the encriptation of the message and do the
     * procedures specified in Castro and Liskov (1999).
     * @param m
     * @return
     */
    private boolean checkPrepare(PBFTMessage m) {

        if (isValidPrepare(m)){
            return gotQuorum(m);
        }

        return false;
    }

    private boolean existsPrePrepare(PBFTMessage m) {
        return ((PBFT)getProtocol()).existsPrePrepare(m);
    }

    private boolean belongsToCurrentView(PBFTMessage m) {
        return ((PBFT)getProtocol()).belongsToCurrentView(m);
    }

    private boolean gotQuorum(PBFTMessage m) {
        return ((PBFT)getProtocol()).gotQuorum(m);
    }

}
