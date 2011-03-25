/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft.acceptors;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.util.JDSUtility;

/**
 *
 * @author aliriosa
 */
public class PBFTPrePrepareAcceptor extends PBFTAcceptor<PBFTPrePrepare>{

    public PBFTPrePrepareAcceptor(PBFT protocol) {
        super(protocol);
    }


    public synchronized boolean accept(PBFTPrePrepare preprepare) {
        
        PBFTServer pbft = (PBFTServer) getProtocol();
        /**
         * If the preprepare hasn't a valid sequence or view number then force a
         * change view.
         */
        if(!(pbft.checkSequenceNumber(preprepare) && pbft.checkViewNumber(preprepare))){
            long nextPP = pbft.getStateLog().getNextPrePrepareSEQ();
            long nextP  = pbft.getStateLog().getNextPrepareSEQ();
            long nextC  = pbft.getStateLog().getNextCommitSEQ();
            long nextE  = pbft.getStateLog().getNextExecuteSEQ();

            JDSUtility.debug(
              "[PBFTPrePrepareAcceptor:accept(preprepare)] s"  + pbft.getLocalServerID() +
              ", at time " + pbft.getClockValue() + ", discarded " + preprepare +
              " because it hasn't a valid sequence/view number. "
              + "(currView = " + pbft.getCurrentViewNumber() + ")"
              + "[nextPP = " + nextPP + ", nextP = " + nextP + ", nextC =" + nextC
              + " , nextE = " + nextE + "]"
            );

            return false;

        }

        /**
         * If the preprepare message wasn't sent by the primary replica then
         * it will be discarded.
         */
        if(!pbft.wasSentByPrimary(preprepare)){
            JDSUtility.debug(
              "[PBFTPrePrepareAcceptor:accept(preprepare)] s"   + pbft.getLocalServerID()   +
              ", at time " + pbft.getClockValue() + ", discarded " + preprepare      +
              " because it wasn't sent by primary server s" + pbft.getCurrentPrimaryID()
            );

            return false;
        }

        if(pbft.updateState(preprepare)){

            /**
             * For each request in batch, check if such request was received.
             */
            for(String digest : preprepare.getDigests()){

                StatedPBFTRequestMessage statedRequest = pbft.getStateLog().getStatedRequest(digest);

                statedRequest.setState(StatedPBFTRequestMessage.RequestState.PREPREPARED);
                statedRequest.setSequenceNumber(preprepare.getSequenceNumber());

                pbft.revokeViewChange(digest);

                JDSUtility.debug(
                  "[PBFTPrePrepareAcceptor:accept(preprepare)] s"  + pbft.getLocalServerID() +
                  ", at time " + pbft.getClockValue() + ", revoked the timeout "
                + "for pre-prepare of " + statedRequest.getRequest()
                );

            }

            pbft.getStateLog().updateNextPrePrepareSEQ(preprepare);

            if(!pbft.isPrimary()){
                pbft.emit(pbft.createPrepareMessage(preprepare), pbft.getLocalGroup());
            }

            return true;
        }

        return false;

    }

}
