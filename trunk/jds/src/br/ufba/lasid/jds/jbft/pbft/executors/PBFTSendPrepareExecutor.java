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
public class PBFTSendPrepareExecutor extends PBFTServerExecutor{

    public PBFTSendPrepareExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage p = (PBFTMessage) act.getWrapper();
        PBFTMessage batch = (PBFTMessage) p.get(PBFTMessage.REQUESTFIELD);

        if(!isServerAuthenticated(p)){
            System.out.println(getDefaultSecurityExceptionMessage(p, "send prepare"));
            return;
        }

        if(!(((PBFT)getProtocol()).isPrimary())){

            getProtocol().getCommunicator().multicast(
                p, ((PBFT)getProtocol()).getLocalGroup()
            );

            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "broadcasts prepare(" + p.get(PBFTMessage.SEQUENCENUMBERFIELD) + ") "
              + "with batch size " + batch.get(PBFTMessage.BATCHSIZEFIELD) + " "
              + "at time " + ((PBFT)getProtocol()).getTimestamp() + " "
              + "to group " + ((PBFT)getProtocol()).getLocalGroup().getGroupID()
            );

        }

    }

}
