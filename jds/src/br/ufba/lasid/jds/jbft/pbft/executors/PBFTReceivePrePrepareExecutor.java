/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferPrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCurrentRoundPhaseTwoAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTReceivePrePrepareExecutor extends PBFTServerExecutor{

    public PBFTReceivePrePrepareExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getWrapper();
        PBFTMessage batch = (PBFTMessage)m.get(PBFTMessage.REQUESTFIELD);

        if(!(((PBFT)getProtocol()).isPrimary())){
        
            System.out.println(
                  "server [p" + getProtocol().getLocalProcess().getID()+"] "
                + "received preprepare(" + m.get(PBFTMessage.SEQUENCENUMBERFIELD) + ") "
                + "with size " + batch.get(PBFTMessage.BATCHSIZEFIELD) + " "
                + "at time "  + ((PBFT)getProtocol()).getTimestamp()
            );

            if(((PBFT)getProtocol()).isValidSequenceNumber(m) && isValidPrePrepare(m)){
                getProtocol().perform(new BufferPrePrepareAction(m));
                getProtocol().perform(new ExecuteCurrentRoundPhaseTwoAction(m));
                return;
            }

            System.out.println(
                  "server [p" + getProtocol().getLocalProcess().getID()+"] "
                + "rejected preprepare(" + m.get(PBFTMessage.SEQUENCENUMBERFIELD) + ") "
                + "because it has a invalid sequence number."
            );

        }
    }

    private boolean isValidPrePrepare(PBFTMessage m) {

        return checkDigest(m) && belongsToCurrentView(m);

    }

    private boolean belongsToCurrentView(PBFTMessage m) {
        return ((PBFT)getProtocol()).belongsToCurrentView(m);
    }

    
}
