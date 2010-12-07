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
public class PBFTSendCommitExecutor extends PBFTServerExecutor{

    public PBFTSendCommitExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage c = (PBFTMessage) act.getWrapper();
        PBFTMessage batch = (PBFTMessage) c.get(PBFTMessage.REQUESTFIELD);

        if(!isServerAuthenticated(c)){
            System.out.println(getDefaultSecurityExceptionMessage(c, "send commit"));
            return;
        }

        c.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVECOMMIT);
        


        getProtocol().getCommunicator().multicast(
            c, ((PBFT)getProtocol()).getLocalGroup()
        );

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "broadcasts commit(" + c.get(PBFTMessage.SEQUENCENUMBERFIELD) + ") "
          + "with batch size " + batch.get(PBFTMessage.BATCHSIZEFIELD) + " "
          + "at time " + ((PBFT)getProtocol()).getTimestamp() + " "
          + "to group " + ((PBFT)getProtocol()).getLocalGroup().getGroupID()
        );

    }
    

}
