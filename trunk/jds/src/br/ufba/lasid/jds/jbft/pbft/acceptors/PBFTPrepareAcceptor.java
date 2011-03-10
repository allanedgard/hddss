/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.acceptors;

import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.util.Debugger;

/**
 *
 * @author aliriosa
 */
public class PBFTPrepareAcceptor extends PBFTAcceptor<PBFTPrepare>{

    public PBFTPrepareAcceptor(PBFT protocol) {
        super(protocol);
    }

    public synchronized boolean accept(PBFTPrepare prepare){

        PBFTServer pbft = (PBFTServer) getProtocol();

        /**
         * If the preprepare hasn't a valid sequence or view number then force a
         * change view.
         */
        if(!(pbft.checkSequenceNumber(prepare) && pbft.checkViewNumber(prepare))){
            long nextPP = pbft.getStateLog().getNextPrePrepareSEQ();
            long nextP  = pbft.getStateLog().getNextPrepareSEQ();
            long nextC  = pbft.getStateLog().getNextCommitSEQ();
            long nextE  = pbft.getStateLog().getNextExecuteSEQ();

            Debugger.debug(
              "[PBFTServer:accept(prepare)] s"  + pbft.getLocalServerID() +
              ", at time " + pbft.getClockValue() + ", discarded " + prepare +
              " because it hasn't a valid sequence/view number. "
              + "(currView = " + pbft.getCurrentViewNumber() + ")"
              + "[nextPP = " + nextPP + ", nextP = " + nextP + ", nextC =" + nextC + " , nextE = " + nextE + "]"
            );

            return false;

        }

        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        if(!pbft.wasSentByAGroupMember(prepare)){
            Debugger.debug(
              "[PBFTServer:accept(prepare)] s"   + pbft.getLocalServerID()   +
              ", at time " + pbft.getClockValue() + ", discarded " + prepare      +
              " because it wasn't sent by a member of the group " + pbft.getLocalGroup()
            );

            return false;
        }

        /**
         * If the prepare message was sent by the primary then it will
         * be discarded.
         */
        if(pbft.wasSentByPrimary(prepare)){
            Debugger.debug(
              "[PBFTServer:accept(prepare)] s"   + pbft.getLocalServerID()   +
              ", at time " + pbft.getClockValue() + ", discarded " + prepare      +
              " because it was sent by the primary " + pbft.getCurrentPrimaryID()
            );

            return false;
        }


        if(!pbft.getStateLog().wasPrePrepared(prepare)){
            Debugger.debug(
              "[PBFTServer:accept(prepare)] s"   + pbft.getLocalServerID()   +
              ", at time " + pbft.getClockValue() + ", discarded " + prepare      +
              " because it hasn't received a related pre-prepare."
            );

            return false;
        }

        if(pbft.updateState(prepare)){

            Long seqn = prepare.getSequenceNumber();

            Quorum q  = pbft.getStateLog().getPrepareQuorum(seqn);

            if(q != null && q.complete()){

                for(String digest : prepare.getDigests()){

                    StatedPBFTRequestMessage statedReq =
                            pbft.getStateLog().getStatedRequest(digest);

                    statedReq.setState(
                            StatedPBFTRequestMessage.RequestState.PREPARED
                    );

                    statedReq.setSequenceNumber(prepare.getSequenceNumber());
                }
                Debugger.debug(
                  "[PBFTServer:accept(prepare)] s" + pbft.getLocalServerID()     +
                  ", at time " + pbft.getClockValue() + ", has just complete " +
                  "the prepare phase for sequence number (" + seqn + ") and "     +
                  "view number (" + prepare.getViewNumber() + ")."
                );

                pbft.getStateLog().updateNextPrepareSEQ(prepare);

                pbft.emit(pbft.createCommitMessage(prepare), pbft.getLocalGroup());

            }
            return true;
        }

        return false;

    }

}
