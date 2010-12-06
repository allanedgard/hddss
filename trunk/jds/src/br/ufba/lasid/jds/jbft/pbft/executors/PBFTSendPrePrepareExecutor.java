/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTSendPrePrepareExecutor extends PBFTServerExecutor{

    public PBFTSendPrePrepareExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage pp = (PBFTMessage) act.getWrapper();
        PBFTMessage batch = (PBFTMessage) pp.get(PBFTMessage.REQUESTFIELD);
        
        if(!isServerAuthenticated(pp)){
            System.out.println(getDefaultSecurityExceptionMessage(pp, "send preprepare"));
            return;
        }

        if(((PBFT)getProtocol()).isPrimary()){

            getProtocol().getCommunicator().multicast(
                pp, ((PBFT)getProtocol()).getLocalGroup()
            );

            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "broadcasts preprepare(" + pp.get(PBFTMessage.SEQUENCENUMBERFIELD) +") "
              + "with batch size " + batch.get(PBFTMessage.BATCHSIZEFIELD) + " "
              + "at time " + ((PBFT)getProtocol()).getTimestamp() + " "
              + "to group " + ((PBFT)getProtocol()).getLocalGroup().getGroupID()
            );

        }
        
    }
    
}
