/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors;

import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.util.Debugger;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTExecutorBroker;

/**
 *
 * @author aliriosa
 */
public class PBFTCommitBrokerServant extends PBFTExecutorBroker<PBFTCommit, PBFTCommit>{


    public PBFTCommitBrokerServant(){

    }

    public PBFTCommitBrokerServant(PBFT p){
        setProtocol(p);
    }


    /**
     * Collect the request sent by the client.
     * @param commit -- the client request.
     */
    protected synchronized boolean accept(PBFTCommit commit){

        PBFTServer pbft = (PBFTServer)getProtocol();

        /**
         * If the preprepare hasn't a valid sequence or view number then force a
         * change view.
         */
        if(!(pbft.hasAValidSequenceNumber(commit) && pbft.hasAValidViewNumber(commit))){

            long nextPP = pbft.getStateLog().getNextPrePrepareSEQ();
            long nextP  = pbft.getStateLog().getNextPrepareSEQ();
            long nextC  = pbft.getStateLog().getNextCommitSEQ();
            long nextE  = pbft.getStateLog().getNextExecuteSEQ();
            Debugger.debug(
              "[PBFTCommitBrokerServant] s"  + pbft.getLocalProcess().getID() +
              ", at time " + pbft.getClock().value() + ", discarded " + commit +
              " because it hasn't a valid sequence/view number. "
              + "(currView = " + pbft.getCurrentViewNumber() + ")"
              + "[nextPP = " + nextPP + ", nextP = "
              + nextP + ", nextC =" + nextC
              + " , nextE = " + nextE + "]"
            );

            return false;

        }

        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        if(!pbft.wasSentByAGroupMember(commit)){
            Debugger.debug(
              "[PBFTCommitBrokerServant] s"   + pbft.getLocalProcess().getID()   +
              ", at time " + pbft.getClock().value() + ", discarded " + commit      +
              " because it wasn't sent by a member of the group " + pbft.getLocalGroup()
            );

            return false;
        }

        if(!pbft.getStateLog().wasPrepared(commit)){
            Debugger.debug(
              "[PBFTCommitBrokerServant] s"   + pbft.getLocalProcess().getID()   +
              ", at time " + pbft.getClock().value() + ", discarded " + commit      +
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
                    

                    Debugger.debug(
                      "[PBFTCommitBrokerServant] s" + pbft.getLocalServerID()     +
                      ", at time " + pbft.getClock().value() + ", has committed " +
                      "and stored request (" + statedReq.getRequest() + ") for " +
                      "processing in view number (" + commit.getViewNumber() + ")."
                    );


                }

                pbft.getStateLog().updateNextCommitSEQ(commit);

                store(commit);

                
                Debugger.debug(
                  "[PBFTCommitBrokerServant] s" + pbft.getLocalServerID()     +
                  ", at time " + pbft.getClock().value() + ", has just complete " +
                  "the commit phase for sequence number (" + seqn + ") and "     +
                  "view number (" + commit.getViewNumber() + ")."
                );
            }
            
            return true;
            
        }

        return false;

    }

    public boolean canConsume(Object object) {

        if(object instanceof PBFTCommit)
            return true;

        if(object instanceof PDU){
            PDU pdu = (PDU) object;
            return canConsume(pdu.getPayload());
        }

        if(object instanceof SignedMessage){
            try{

                SignedMessage m = (SignedMessage) object;

                return canConsume(m.getSignedObject().getObject());

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        return false;
    }

}
