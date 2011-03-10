/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.acceptors;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IStore;
import br.ufba.lasid.jds.util.Debugger;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdbm.helper.Tuple;
import trash.br.ufba.lasid.jds.comm.QuorumTable;

/**
 *
 * @author aliriosa
 */
public class PBFTStatusActiveAcceptor extends PBFTAcceptor<PBFTStatusActive>{

    public PBFTStatusActiveAcceptor(PBFT protocol) {
        super(protocol);
    }

    public synchronized boolean accept(PBFTStatusActive stsActive) {
        PBFTServer pbft = (PBFTServer) getProtocol();
        IProcess rServer = new br.ufba.lasid.jds.Process(stsActive.getReplicaID());

        if(stsActive != null && !pbft.getLocalServerID().equals(rServer.getID())){

            /**
             * If the preprepare hasn't a valid sequence or view number then force a
             * change view.
             */
            if(!pbft.checkViewNumber(stsActive)){
                long nextPP = pbft.getStateLog().getNextPrePrepareSEQ();
                long nextP  = pbft.getStateLog().getNextPrepareSEQ();
                long nextC  = pbft.getStateLog().getNextCommitSEQ();
                long nextE  = pbft.getStateLog().getNextExecuteSEQ();
                long lwSEQ  = pbft.getCheckpointLowWaterMark();

                Debugger.debug(
                  "[PBFTServer:accept(activeStatus)] s"  + pbft.getLocalServerID() +
                  ", at time " + pbft.getClockValue() + ", discarded " + stsActive +
                  " because it hasn't a valid view number. "
                  + "(currView = " + pbft.getCurrentViewNumber() + ")"
                  + "[nextPP = " + nextPP + ", nextP = "
                  + nextP + ", nextC =" + nextC
                  + " , nextE = " + nextE + ", lowWaterMark = " + lwSEQ + "]"
                );

                return false;

            }

            /**
             * If the preprepare message wasn't sent by a group member then it will
             * be discarded.
             */
            if(!pbft.wasSentByAGroupMember(stsActive)){
                Debugger.debug(
                  "[PBFTServer:accept(activeStatus)] s"   + pbft.getLocalServerID()   +
                  ", at time " + pbft.getClockValue() + ", discarded " + stsActive      +
                  " because it wasn't sent by a member of " + pbft.getLocalGroup()
                );

                return false;
            }

            /**
             * Send the executed request.
             */
            if(pbft.getStateLog().getNextPrepareSEQ() >= 0){
                Long maxSEQ = pbft.getStateLog().getNextPrePrepareSEQ();
                Long minSEQ = pbft.getStateLog().getNextExecuteSEQ()-1;

                long eSEQ  = stsActive.getLastExecutedSEQ();
                long cSEQ  = stsActive.getLastCommittedSEQ();
                long pSEQ  = stsActive.getLastPreparedSEQ();
                long ppSEQ = stsActive.getLastPrePreparedSEQ();
                long lwSEQ   = stsActive.getLastStableCheckpointSEQ();

                if(minSEQ > eSEQ ) minSEQ = eSEQ;
                if(minSEQ > cSEQ ) minSEQ = cSEQ;
                if(minSEQ > pSEQ ) minSEQ = pSEQ;
                if(minSEQ > ppSEQ) minSEQ = ppSEQ;
                if(minSEQ < 0L)    minSEQ = 0L;

                if(maxSEQ < eSEQ ) maxSEQ = eSEQ;
                if(maxSEQ < cSEQ ) maxSEQ = cSEQ;
                if(maxSEQ < pSEQ ) maxSEQ = pSEQ;
                if(maxSEQ < ppSEQ) maxSEQ = ppSEQ;

                if(maxSEQ < 0L) maxSEQ = 0L;

                long _eSEQ  = pbft.getStateLog().getNextExecuteSEQ() - 1L;
                long _cSEQ  = pbft.getStateLog().getNextCommitSEQ()  - 1L;
                long _pSEQ  = pbft.getStateLog().getNextPrepareSEQ() - 1L;
                long _ppSEQ = pbft.getStateLog().getNextPrePrepareSEQ() - 1L;
                long _lwSEQ = pbft.getCheckpointLowWaterMark();

                QuorumTable qtable = pbft.getStateLog().getQuorumTable(PBFT.CHECKPOINTQUORUMSTORE);

                PBFTBag bag = new PBFTBag(pbft.getLocalServerID());

                boolean sent = false;
                try{
                    for(long i = minSEQ; i < maxSEQ; i++){

                        PBFTLogEntry entry = pbft.getStateLog().get(i);

                        if(entry != null){
                            Quorum pq = entry.getPrepareQuorum();
                            Quorum cq = entry.getCommitQuorum();


                            if(cSEQ < i && pSEQ < i && ppSEQ <= i && pbft.isPrimary() && entry.getPrePrepare() != null && _ppSEQ >= i){
                                PBFTPrePrepare pp = entry.getPrePrepare();
                                bag.addMessage(pp);
                                //emit(pp);
                                sent = sent || true;
                            }

                            if(cSEQ < i && pSEQ <= i && pq != null/*&& _pSEQ >= i*/){
                                Quorum q = new Quorum();
                                q.addAll(pq);
                                if(!q.isEmpty()){

                                    for(IMessage m : q){

                                        PBFTPrepare p = (PBFTPrepare)m;

                                        if(p.getReplicaID().equals(pbft.getLocalServerID())){
                                            bag.addMessage(p);
                                            //emit(p);
                                            sent = sent || true;
                                        }
                                    }
                                }
                            }

                            if(cSEQ <= i && cq != null/*&& _cSEQ >=i*/){

                                Quorum q = new Quorum();
                                q.addAll(cq);

                                if(!q.isEmpty()){
                                    for(IMessage m : q){

                                        PBFTCommit c = (PBFTCommit)m;

                                        if(c.getReplicaID().equals(pbft.getLocalServerID())){
                                            bag.addMessage(c);
                                            //emit(c);
                                            sent = sent || true;
                                        }
                                    }
                                }
                            }
                        }//end if entry
                    }//end for seq

                    long currSEQ = lwSEQ + 1;
                    IStore store = pbft.getCheckpointStore();

                    while(currSEQ < _lwSEQ){
                        try {
                            String index = String.valueOf(currSEQ) + ";" + "null";

                            Tuple tuple = store.findGreaterOrEqual(index);

                            if(tuple == null){
                                break;
                            }

                            String key = (String)tuple.getKey();
                            String[] pair = key.split(";");

                            Long seqn = Long.valueOf(pair[0]);

                            String digest = pair[1];

                            PBFTCheckpoint checkpoint = new PBFTCheckpoint(seqn, digest, pbft.getLocalServerID());

                            //emit(checkpoint);
                            bag.addMessage(checkpoint);

                            currSEQ = seqn + 1;

                        } catch (Exception ex) {
                            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                            ex.printStackTrace();
                        }
                    }//end while currSEQ < _lwSEQ, if currSEQ_0 = lwSEQ
                }catch(Exception ex){
                    Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                }
                if(!bag.isEmpty()){
                    pbft.emit(bag, rServer);
                }
            }
        }

        return true;
    }
}
