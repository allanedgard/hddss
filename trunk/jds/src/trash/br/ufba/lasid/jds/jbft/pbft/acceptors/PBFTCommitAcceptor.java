/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft.acceptors;

import br.ufba.lasid.hdf.ISupplier;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.util.JDSUtility;

/**
 *
 * @author aliriosa
 */
public class PBFTCommitAcceptor extends PBFTAcceptor<PBFTCommit>{

    public PBFTCommitAcceptor(PBFT protocol) {
        super(protocol);
    }

    ISupplier supplier;
    public void setSupplier(ISupplier supplier){
        this.supplier = supplier;
    }

    public synchronized boolean accept(PBFTCommit commit) {
        PBFTServer pbft = (PBFTServer) getProtocol();
        /**
         * If the preprepare hasn't a valid sequence or view number then force a
         * change view.
         */
        if(!(pbft.checkSequenceNumber(commit) && pbft.checkViewNumber(commit))){

            long nextPP = pbft.getStateLog().getNextPrePrepareSEQ();
            long nextP  = pbft.getStateLog().getNextPrepareSEQ();
            long nextC  = pbft.getStateLog().getNextCommitSEQ();
            long nextE  = pbft.getStateLog().getNextExecuteSEQ();
            JDSUtility.debug(
              "[PBFTServer:accept(commit)] s"  + pbft.getLocalServerID() +
              ", at time " + pbft.getClockValue() + ", discarded " + commit +
              " because it hasn't a valid sequence/view number. "
              + "(currView = " + pbft.getCurrentViewNumber() + ")"
              + "[nextPP = " + nextPP + ", nextP = " + nextP + ", nextC =" + nextC
              + " , nextE = " + nextE + "]"
            );

            return false;

        }

        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        if(!pbft.wasSentByAGroupMember(commit)){
            JDSUtility.debug(
              "[PBFTServer:accept(commit)] s"   + pbft.getLocalServerID()   +
              ", at time " + pbft.getClockValue() + ", discarded " + commit      +
              " because it wasn't sent by a member of the group " + pbft.getLocalGroup()
            );

            return false;
        }

        if(!pbft.getStateLog().wasPrepared(commit)){
            JDSUtility.debug(
              "[PBFTServer:accept(commit)] s"   + pbft.getLocalServerID()   +
              ", at time " + pbft.getClockValue() + ", discarded " + commit      +
              " because it hasn't received a related pre-prepare."
            );

            return false;
        }

        if(pbft.updateState(commit)){

            Long seqn = commit.getSequenceNumber();

            Quorum q  = pbft.getStateLog().getCommitQuorum(seqn);

            if(q != null && q.complete()){

                PBFTPrePrepare  pp = pbft.getStateLog().getPrePrepare(seqn);

                for(String digest : pp.getDigests()){

                    StatedPBFTRequestMessage statedReq =
                            pbft.getStateLog().getStatedRequest(digest);

                    statedReq.setState(
                            StatedPBFTRequestMessage.RequestState.COMMITTED
                    );

                    statedReq.setSequenceNumber(commit.getSequenceNumber());


                    JDSUtility.debug(
                      "[PBFTServer:accept(commit)] s" + pbft.getLocalServerID()     +
                      ", at time " + pbft.getClockValue() + ", has committed " +
                      "and stored request (" + statedReq.getRequest() + ") for " +
                      "processing in view number (" + commit.getViewNumber() + ")."
                    );


                }

                pbft.getStateLog().updateNextCommitSEQ(commit);

                supplier.getOutbox().add(commit);


                JDSUtility.debug(
                  "[PBFTServer:accept(commit)] s" + pbft.getLocalServerID()     +
                  ", at time " + pbft.getClockValue() + ", has just complete " +
                  "the commit phase for sequence number (" + seqn + ") and "     +
                  "view number (" + commit.getViewNumber() + ")."
                );

                return true;
            }

        }

        return false;

    }

}
