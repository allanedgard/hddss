/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpointMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTSendCheckpointExecutor extends PBFTServerExecutor{
    public PBFTSendCheckpointExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        Long lastStableSeq = ((PBFT)getProtocol()).getLastStableStateSequenceNumber();
        Integer currentView   = ((PBFT)getProtocol()).getCurrentView();

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID() + "] " 
          + "has last stable sequence number equals to " + lastStableSeq + " "
          + "at time " + ((PBFT)getProtocol()).getTimestamp()
        );

        PBFTMessage checkpoint = new PBFTCheckpointMessage();
        
        checkpoint.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVECHECKPOINT);
        checkpoint.put(PBFTMessage.SEQUENCENUMBERFIELD, lastStableSeq);
        checkpoint.put(PBFTMessage.VIEWFIELD, currentView);
        checkpoint.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());

        checkpoint = makeDisgest(checkpoint);
        checkpoint = encrypt(checkpoint);

        Group g = ((PBFT)getProtocol()).getLocalGroup();

        getProtocol().getCommunicator().multicast(checkpoint, g);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID() + "] "
          + "sent <checkpoint, last-stable-seqn = " + lastStableSeq + ", "
          + "digest = " + checkpoint.get(PBFTMessage.DIGESTFIELD) + ", "
          + "replica = " + checkpoint.get(PBFTMessage.REPLICAIDFIELD) + "> "
          + "to group [" + g.getGroupID() + "] "
          + "at time " + ((PBFT)getProtocol()).getTimestamp()
        );


    }



}
