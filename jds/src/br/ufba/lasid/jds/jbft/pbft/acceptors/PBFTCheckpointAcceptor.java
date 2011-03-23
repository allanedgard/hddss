/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.acceptors;

import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.util.JDSUtility;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckpointAcceptor extends PBFTAcceptor<PBFTCheckpoint>{

    public PBFTCheckpointAcceptor(PBFT protocol) {
        super(protocol);
    }

    public boolean accept(PBFTCheckpoint checkpoint) {
        PBFTServer pbft = (PBFTServer)getProtocol();
        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        if(!pbft.wasSentByAGroupMember(checkpoint)){
            JDSUtility.debug(
              "[PBFTServer:accept(checkpoint)] s"   + pbft.getLocalServerID()   +
              ", at time " + pbft.getClockValue() + ", discarded " + checkpoint      +
              " because it wasn't sent by a member of the group " + pbft.getLocalGroup()
            );

            return false;
        }

        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        long lowWaterMark = pbft.getCheckpointLowWaterMark();
        long highWaterMark = pbft.getCheckpointHighWaterMark();
        long seqn = checkpoint.getSequenceNumber();

        if(lowWaterMark > seqn){
            JDSUtility.debug(
              "[PBFTServer:accept(checkpoint)] s"   + pbft.getLocalServerID()     +
              ", at time " + pbft.getClockValue() + ", discarded " + checkpoint +
              " because it has sequence number lower than current low water mark " +
              "(LCWM = " + lowWaterMark + "). "
            );

            return false;
        }

        pbft.updateState(checkpoint);

        String entryKey = checkpoint.getSequenceNumber().toString();

        Quorum q  = pbft.getStateLog().getQuorum(PBFT.CHECKPOINTQUORUMSTORE, entryKey);

        if(q != null && q.complete()){

            JDSUtility.debug(
              "[PBFTServer:accept(checkpoint)] s" + pbft.getLocalServerID()     +
              ", at time " + pbft.getClockValue() + ", has already complete  a quorum for " +
              " checkpoint with sequence number (" + seqn + ")."
            );

            if(seqn > highWaterMark){
                JDSUtility.debug(
                  "[PBFTServer:accept(checkpoint)] s" + pbft.getLocalServerID()     +
                  ", at time " + pbft.getClockValue() + ", has detected a stable " +
                  " checkpoint certificate with sequence number (" + seqn + ") " +
                  "greater than its high checkpoint water mark (HCWK = " + highWaterMark + ")."
                );
                JDSUtility.debug(
                  "[PBFTServer:accept(checkpoint)] s" + pbft.getLocalServerID()     +
                  ", at time " + pbft.getClockValue() + ", is going to start " +
                  " a start transfer procedure."
                );
//                pbft.setLockCheckpoint(true);
                //pbft.emit(pbft.createFetchMessage(), pbft.getLocalGroup());
                return false;
            }

            pbft.doCheckpoint(seqn);

            return true;
        }

        return false;


    }



}
