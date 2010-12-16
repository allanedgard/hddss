/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTTuple;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteChangeViewRoundTwoAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveChangeViewExecutor extends PBFTServerExecutor{

    public PBFTReceiveChangeViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    /**
     * [TODO]
     * @param act
     */
    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getWrapper();
        
        Buffer Q = (Buffer) m.get(PBFTMessage.SETPREPREPAREINFORMATIONFIELD);
        Buffer P = (Buffer) m.get(PBFTMessage.SETPREPAREINFORMATIONFIELD);
        Buffer C = (Buffer) m.get(PBFTMessage.SETCHECKPOINTEDINFORMATIONFIELD);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "has received <CHANGE-VIEW,  view = "
          + m.get(PBFTMessage.VIEWFIELD) + ", digest = "
          + m.get(PBFTMessage.DIGESTFIELD) + ", checkpoint-low-water-mark = "
          + m.get(PBFTMessage.CHECKPOINTLOWWATERMARK) + ", "
          + "P (size = " + P.size() + "), Q (size = " + Q.size() + "), "
          + "C (size = " + C.size() + ") replica = "
          + m.get(PBFTMessage.REPLICAIDFIELD) + ">"
        );

        getProtocol().perform(new BufferChangeViewAction(m));

        if(checkChangeView(m)){

            getProtocol().perform(new ExecuteChangeViewRoundTwoAction(m));
            
        }
            
    }
    private boolean checkChangeView(PBFTMessage cv) {
        
        if(checkDigest(cv)){

            Buffer P = (Buffer) cv.get(PBFTMessage.SETPREPAREINFORMATIONFIELD);
            Buffer Q = (Buffer) cv.get(PBFTMessage.SETPREPREPAREINFORMATIONFIELD);
            
            return (
               hasViewConsistentInformation(P) &&
               hasViewConsistentInformation(Q) &&
               gotQuorum(cv)

            );

        }

        return false;
    }

    private boolean gotQuorum(PBFTMessage cv) {
        return ((PBFT)getProtocol()).gotQuorum(cv);
    }

    private boolean hasViewConsistentInformation(Buffer set) {
        return ((PBFT)getProtocol()).hasViewConsistentInformation(set);
    }
}

