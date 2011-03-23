/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.acceptors;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.util.JDSUtility;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class PBFTDoerAcceptor extends PBFTAcceptor<Long>{

    public PBFTDoerAcceptor(PBFT protocol) {
        super(protocol);
    }

    public synchronized boolean accept(Long seqn) {
        PBFTServer pbft = (PBFTServer) getProtocol();
        try{

            long startSEQ = pbft.getStateLog().getNextExecuteSEQ();
            long finalSEQ = seqn;

            for(long currSEQ = startSEQ; currSEQ <= finalSEQ; currSEQ++ ){

                PBFTLogEntry entry = pbft.getStateLog().get(currSEQ);

                if(!(entry != null && entry.getPrePrepare() != null)){
                    return false;
                }

                Quorum pq = entry.getPrepareQuorum();
                Quorum cq = entry.getCommitQuorum();

                if(!(pq != null && pq.complete() && cq != null & cq.complete())){
                    return false;
                }

                PBFTPrePrepare preprepare = entry.getPrePrepare();

                for(String digest : preprepare.getDigests()){

                    StatedPBFTRequestMessage statedReq = pbft.getStateLog().getStatedRequest(digest);

                    PBFTRequest request = statedReq.getRequest();

                    if(!statedReq.getState().equals(StatedPBFTRequestMessage.RequestState.COMMITTED)){
                        JDSUtility.debug(
                          "[PBFTServer:execute(seqn)] s"  + pbft.getLocalServerID()   +
                          ", at time " + pbft.getClockValue() + ", couldn't " +
                          "execute " + request + " because it hasn't been " +
                          "committed yet in current view " +
                          "(currView = " + pbft.getCurrentViewNumber() + ")"
                        );

                        return false;
                    }

                }//end for each digest (check committed)

                for(String digest : preprepare.getDigests()){

                    StatedPBFTRequestMessage statedReq = pbft.getStateLog().getStatedRequest(digest);

                    PBFTRequest request = statedReq.getRequest();

                    IServer lServer = pbft.getServer();

                    IPayload result = lServer.executeCommand(request.getPayload());


                    PBFTReply reply = pbft.createReplyMessage(request, result);

                    statedReq.setState(StatedPBFTRequestMessage.RequestState.SERVED);
                    statedReq.setReply(reply);

                    JDSUtility.debug(
                      "[PBFTServer:execute(seqn)] s"  + pbft.getLocalServerID()       +
                      ", at time " + pbft.getClockValue() + ", executed " + request +
                      " (currView = " + pbft.getCurrentViewNumber() + ")"
                    );

                    IProcess client = new br.ufba.lasid.jds.BaseProcess(reply.getClientID());
                    pbft.emit(reply, client);

                }//end for each digest (handle and reply)

                pbft.getStateLog().updateNextExecuteSEQ(currSEQ);

                long execSEQ = pbft.getStateLog().getNextExecuteSEQ() -1;
                long chkpSEQ = pbft.getCheckpointPeriod();

                if(execSEQ > 0 && ((execSEQ % chkpSEQ) == 0)){
                    pbft.emit(pbft.createCheckpointMessage(execSEQ), pbft.getLocalGroup());
                }

            }//end for each seqn

            return true;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;

    }

}
