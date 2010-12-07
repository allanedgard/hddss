/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.jbft.pbft.actions.BufferCheckpointAction;
import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.CheckStateAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveCheckpointExecutor extends PBFTServerExecutor{

    public PBFTReceiveCheckpointExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage checkpoint = (PBFTMessage) act.getWrapper();

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID() + "] "
          + "received <checkpoint, "
          + checkpoint.get(PBFTMessage.SEQUENCENUMBERFIELD)
          + ", " + checkpoint.get(PBFTMessage.DIGESTFIELD) + ", "
          + checkpoint.get(PBFTMessage.REPLICAIDFIELD) + "> "
          + "from server [" + checkpoint.get(PBFTMessage.REPLICAIDFIELD) + "] "
          + "at time " + ((PBFT)getProtocol()).getTimestamp()
        );

        getProtocol().perform(new BufferCheckpointAction(checkpoint));
        getProtocol().perform(new CheckStateAction(checkpoint));

        
    }



}
