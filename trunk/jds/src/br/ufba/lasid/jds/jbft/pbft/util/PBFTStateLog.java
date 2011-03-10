/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.comm.Quorum;
import trash.br.ufba.lasid.jds.comm.QuorumTable;
import trash.br.ufba.lasid.jds.comm.QuorumTableStore;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage.RequestState;
import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IState;
import br.ufba.lasid.jds.util.DigestList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class PBFTStateLog extends Hashtable<Long, PBFTLogEntry>{
    
    private static final long serialVersionUID = 9080466116863750014L;

     QuorumTableStore qstore = new QuorumTableStore();
    
    private static String PREPAREQUORUMTABLE = "__PREPAREQUORUMTABLE";
    private static String COMMITQUORUMTABLE = "__COMMITQUORUMTABLE";
    private static String CHECKPOINTQUORUMTABLE = "__CHECKPOINTQUORUMTABLE";
    private static String REPLYQUORUMTABLE = "__REPLYQUORUMTABLE";
       
    private int lastChangeViewTimestamp = -1;

    private long cpLowWaterMark = -1;

    private  PBFTRequestTable rseqtable = new PBFTRequestTable();
    private  PBFTRequestTable rdigtable = new PBFTRequestTable();
    private  PBFTCheckpointTable cptable = new PBFTCheckpointTable();
    
    public void doCacheServerState(Long seqn, String digest, IState state){

        PBFTCheckpointTuple ctuple = new PBFTCheckpointTuple(seqn, digest, state);
        
        cptable.put(seqn, ctuple);

    }

    public PBFTCheckpointTable getCachedState(){
        return cptable;
    }

    protected  long nextPrePrepareSEQ =  0L;
    protected  long nextPrepareSEQ    =  0L;
    protected  long nextCommitSEQ     =  0L;
    protected  long nextExecuteSEQ    =  0L;
    
    public void updateNextPrePrepareSEQ(PBFTPrePrepare m){
        synchronized(this){
            if(m != null && m.getSequenceNumber() != null){
                long seqn = m.getSequenceNumber();
                if(seqn == nextPrePrepareSEQ){
                    nextPrePrepareSEQ++;
                }
            }
        }
    }

    public void updateNextPrepareSEQ(PBFTPrepare m){
        synchronized(this){
            if(m != null && m.getSequenceNumber() != null){
                long seqn = m.getSequenceNumber();
                if(seqn < nextPrePrepareSEQ && seqn == nextPrepareSEQ){
                    nextPrepareSEQ++;
                }
            }
        }
    }

    public void updateNextCommitSEQ(PBFTCommit m){
        synchronized(this){
            if(m != null && m.getSequenceNumber() != null){
                long seqn = m.getSequenceNumber();
                if(seqn < nextPrePrepareSEQ && seqn < nextPrepareSEQ && seqn == nextCommitSEQ){
                    nextCommitSEQ++;
                }
            }
        }
    }

    public void updateNextExecuteSEQ(Long theSEQ){
        synchronized(this){
            if(theSEQ != null){
                long seqn = theSEQ;
                if(seqn < nextPrePrepareSEQ && seqn < nextPrepareSEQ && seqn < nextCommitSEQ && seqn == nextExecuteSEQ){
                    nextExecuteSEQ++;
                }
            }
        }
    }

    public void setNextCommitSEQ(long nextCommitSEQ) {
        this.nextCommitSEQ = nextCommitSEQ;
    }

    public void setNextExecuteSEQ(long nextExecuteSEQ) {
        this.nextExecuteSEQ = nextExecuteSEQ;
    }

    public void setNextPrePrepareSEQ(long nextPrePrepareSEQ) {
        this.nextPrePrepareSEQ = nextPrePrepareSEQ;
    }

    public void setNextPrepareSEQ(long nextPrepareSEQ) {
        this.nextPrepareSEQ = nextPrepareSEQ;
    }


    public long getNextCommitSEQ() {
        return nextCommitSEQ;
    }

    public long getNextPrePrepareSEQ() {
        return nextPrePrepareSEQ;
    }

    public long getNextPrepareSEQ() {
        return nextPrepareSEQ;
    }

    public long getNextExecuteSEQ() {
        return nextExecuteSEQ;
    }


    public boolean wasPrePrepared(PBFTPrepare p){
        synchronized(this){
            /**
             * If not is a null prepare
             */
            if(p != null){

                PBFTLogEntry entry = get(p.getSequenceNumber());

                /**
                 * If the prepare has request digests and there's a entry in the log
                 * for the related sequence number.
                 */
                if(p.getDigests() != null && entry != null){

                    PBFTPrePrepare pp = entry.getPrePrepare();
                    /**
                     * If there's a preprepare message in the log entry and this
                     * preprepare has the same view and sequence number of the cur-
                     * rent prepare. Moreover, the preprepare and prepare have to
                     * have to same number of digests.
                     */
                    if(
                            pp != null &&
                            pp.getSequenceNumber().equals(p.getSequenceNumber()) &&
                            pp.getViewNumber().equals(p.getViewNumber()) &&
                            pp.getDigests().size() == p.getDigests().size()
                    ){
                        /**
                         * Each digest in the current prepare message has to be a
                         * request the was preprepared, prepared, commited or served
                         * and such digest has to in the related preprepare.
                         */
                        for(String digest : p.getDigests()){
                            StatedPBFTRequestMessage statedReq =  getStatedRequest(digest);
                            if((
                                !statedReq.getState().equals(RequestState.PREPREPARED) &&
                                !statedReq.getState().equals(RequestState.PREPARED)    &&
                                !statedReq.getState().equals(RequestState.COMMITTED)   &&
                                !statedReq.getState().equals(RequestState.SERVED)
                            )){
                              return false;
                            }

                            /**
                             * If the prepare has a digest that wasn't pre-prepared
                             * then the prepare wasn't pre-prepared.
                             */
                            if(!pp.getDigests().contains(digest)){

                                return false;

                            }
                        }

                        /**
                         * All the request that in the current prepare were
                         * pre-prepared.
                         */

                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean wasPrepared(PBFTCommit c){
        synchronized(this){
            /**
             * If not is a null prepare
             */
            if(c != null){

                PBFTLogEntry entry = get(c.getSequenceNumber());

                /**
                 * If the prepare has request digests and there's a entry in the log
                 * for the related sequence number.
                 */
                if(entry != null){

                    PBFTPrePrepare pp = entry.getPrePrepare();
                    /**
                     * If there's a preprepare message in the log entry and this
                     * preprepare has the same view and sequence number of the cur-
                     * rent prepare. Moreover, the preprepare and prepare have to
                     * have to same number of digests.
                     */
                    if(
                            pp != null &&
                            pp.getSequenceNumber().equals(c.getSequenceNumber()) &&
                            pp.getViewNumber().equals(c.getViewNumber())
                    ){

                        /**
                         * All the request that in the current prepare were
                         * pre-prepared.
                         */

                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void insertRequestInTables(String digest, PBFTRequest r, RequestState rstate){
        synchronized(this){

            if(digest != null){

                PBFTRequestTuple rtuple = rdigtable.get(digest);

                if(rtuple == null){
                    rtuple = new PBFTRequestTuple();
                    rdigtable.put(digest, rtuple);
                    rseqtable.put(r.getClientID(), rtuple);

                    StatedPBFTRequestMessage m =
                            new StatedPBFTRequestMessage(r, rstate, digest);

                    rtuple.put(r.getTimestamp(), m);
                }

            }
        }
    }

    public StatedPBFTRequestMessage getStatedRequest(String digest){
        return (StatedPBFTRequestMessage)rdigtable.get(digest).values().toArray()[0];
    }

    public StatedPBFTRequestMessage getStatedRequest(PBFTRequest r){
        return getStateRequest(r.getClientID(), r.getTimestamp());
    }

    public StatedPBFTRequestMessage getStateRequest(Object clientID, Long timestamp){
        PBFTRequestTuple rtuple = rseqtable.get(clientID);

        if(rtuple != null){
            return rtuple.get(timestamp);
        }

        return null;
        
    }
    public boolean hasARequestWaiting(String digest){
        return hasARequestInSuchState(digest, RequestState.WAITING);
    }

    public boolean hasARequestPrePrepared(String digest) {
        return hasARequestInSuchState(digest, RequestState.PREPREPARED);
    }

    public boolean hasARequestInSuchState(String digest, RequestState state){
        PBFTRequestTuple rtuple = rdigtable.get(digest);

        if(rtuple != null){

            if(rtuple.values().size() == 1){

                StatedPBFTRequestMessage m = rtuple.values().iterator().next();

                return m.getState().equals(state);
            }
        }

        return false;        
    }

    public Quorum getPrepareQuorum(Long seqn){
        synchronized(this){
        
            PBFTLogEntry entry = get(seqn);

            if(entry != null){
                return entry.getPrepareQuorum();
            }
        }
        return null;
    }

    public Quorum getCommitQuorum(Long seqn){

        synchronized(this){
            PBFTLogEntry entry = get(seqn);

            if(entry != null){
                return entry.getCommitQuorum();
            }
        }
        return null;
    }

    public PBFTPrePrepare getPrePrepare(Long seqn){
        synchronized(this){
            PBFTLogEntry entry = get(seqn);

            if(entry != null){
                return entry.getPrePrepare();
            }
        }
        return null;

    }

    public void setCheckpointLowWaterMark(long mark){
        synchronized(this){
            if(cpLowWaterMark < mark)
                cpLowWaterMark = mark;
        }
    }
    
    public long getCheckpointLowWaterMark(){
        return cpLowWaterMark;
    }

    public long getCheckpointHighWaterMark(long checkpointPeriod, long factor){
            return cpLowWaterMark + factor * checkpointPeriod;
    }

//    public synchronized void putInBacklog(PBFTRequest req){
//        changeRequestStatus(req);
//    }

    public PBFTReply getReplyInRequestTable(PBFTRequest r){
        return getReplyInRequestTable(r.getClientID(), r.getTimestamp());
    }

    public PBFTReply getReplyInRequestTable(Object clientID, Long timestamp){
        try{
            synchronized(this){
                PBFTRequestTuple rtuple = rseqtable.get(clientID);
                StatedPBFTRequestMessage stateReq = rtuple.get(timestamp);
                return stateReq.getReply();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return null;

        }

    }
    /*
    public void changeRequestStatus(IMessage m) {
        
        if(m instanceof PBFTRequest){
            changeRequestStatus((PBFTRequest)m);
        }

        if(m instanceof PBFTPrepare){
            changeRequestStatus((PBFTPrepare)m);
        }

        if(m instanceof PBFTCommit){
            changeRequestStatus((PBFTCommit)m);
        }
        
        if(m instanceof PBFTPrePrepare){
            changeRequestStatus((PBFTPrePrepare)m);
        }

        if(m instanceof PBFTReply){
            changeRequestStatus((PBFTReply)m);
        }

        if(m instanceof PBFTCheckpoint){
            changeRequestStatus((PBFTCheckpoint)m);
        }


    }
     */

    public boolean isWaiting(PBFTRequest r){
        return inState(r, RequestState.WAITING);
    }

    public boolean wasPrePrepared(PBFTRequest r){
        return inState(r, RequestState.PREPREPARED);
    }

    public boolean wasPrepared(PBFTRequest r){
        return inState(r, RequestState.PREPARED);
    }

    public boolean wasCommitted(PBFTRequest r){
        return inState(r, RequestState.COMMITTED);
    }

    public boolean wasServed(PBFTRequest r){
        return inState(r, RequestState.SERVED);
    }

    public boolean inState(PBFTRequest r, RequestState rstate){

        try{
            if(rseqtable != null && !rseqtable.isEmpty()){

                PBFTRequestTuple rtuple = rseqtable.get(r.getClientID());

                if(rtuple != null && !rtuple.isEmpty()){
                    StatedPBFTRequestMessage statedReq = rtuple.get(r.getTimestamp());

                    if(statedReq != null && statedReq.getState() != null){
                        return statedReq.getState().equals(rstate);
                    }

                }
            }
            
            return false;

        }catch(Exception ex){
            ex.printStackTrace();
            return false;

        }
        
    }

    public boolean wasAccepted(PBFTRequest r){

        try{

            return !(!isWaiting(r) && !wasPrePrepared(r) && !wasPrepared(r) && !wasCommitted(r) && !wasServed(r));

        }catch(Exception ex){
            ex.printStackTrace();
            return false;
            
        }
        
    }

    public Quorum getQuorum(String qtname, String qname){

        QuorumTable qtable = getQuorumTable(qtname);
        Quorum quorum = qtable.get(qname);
        
        return quorum;
    }

    public QuorumTable getQuorumTable(String name){

        synchronized(this){
            QuorumTable qtable = qstore.get(name);

            if(qtable == null){

                qtable = new QuorumTable();
                qstore.put(name, qtable);
            }

            return qtable;
        }
        
    }
    

    public void setLastChangeViewTimestamp(int lastChangeViewTimestamp) {
        this.lastChangeViewTimestamp = lastChangeViewTimestamp;
    }


    public int getLastChangeViewTimestamp() {
        return lastChangeViewTimestamp;
    }


    /**
     * Check if the request no more belongs to the PBFT' Log state.
     * @param request -- the request.
     * @return true if no more in the PBFT' Log state.
     */
    public boolean noMore(PBFTRequest request) {

        StatedPBFTRequestMessage statedReq = getStatedRequest(request);

        if(statedReq == null){
            PBFTRequestTuple rtuple = rseqtable.get(request.getClientID());
            if(rtuple != null){
                ArrayList<Long> timestamps =  new ArrayList<Long>(rtuple.keySet());
                Collections.sort(timestamps);

                long myTimestamp = timestamps.get(timestamps.size() - 1);
                long theTimestamp = request.getTimestamp();
                
                return myTimestamp > theTimestamp;
            }
        }

        return false;
        
    }

//    protected synchronized boolean hasLowestTimestamp(PBFTRequest r){
//
//        try{
//
//            PBFTRequestTuple rtuple = rseqtable.get(r.getClientID());
//
//            ArrayList<Long> myTimestamps = new ArrayList<Long>(rtuple.keySet());
//
//            Collections.sort(myTimestamps);
//
//            long maxTimestamp = myTimestamps.get(myTimestamps.size()-1);
//            long theTimestamp = r.getTimestamp();
//
//            return maxTimestamp > theTimestamp;
//
//
//        }catch(Exception e){
//            e.printStackTrace();
//            return false;
//        }
//
//    }

    public void doGarbage() {
        synchronized(this){
            long finalSEQ = getCheckpointLowWaterMark();

            ArrayList<Long> seqns = new ArrayList<Long>();
            seqns.addAll(keySet());

            Collections.sort(seqns);

            if(!seqns.isEmpty()){

                long startSEQ = seqns.get(0);
                QuorumTable qtable = null;

                for(long seq = startSEQ; seq <= finalSEQ; seq++){
                    PBFTLogEntry entry = get(seq);
                    DigestList digests = new DigestList();


                    digests.addAll(entry.getPrePrepare().getDigests());
                    //Debugger.debug("\t removing messages for sequence number " + seq);
                    //Debugger.debug("\t\t cleaning up the related client requests" + seq);
                    for(String digest : digests){
                        rseqtable.remove(seq);
                        rdigtable.remove(digest);
                    }

                    //Debugger.debug("\t\t cleaning up the prepare / commit / checkpoint message quorums ...");

                    qtable = qstore.get(PREPAREQUORUMTABLE);    if(qtable != null) qtable.remove(seq);
                    qtable = qstore.get(COMMITQUORUMTABLE);     if(qtable != null) qtable.remove(seq);
                    qtable = qstore.get(CHECKPOINTQUORUMTABLE); if(qtable != null) qtable.remove(seq);

                    remove(seq);

                    if(seq < finalSEQ){
                        //Debugger.debug("\t\t cleaning up the cached checkpoints ...");
                        cptable.remove(seq);
                    }

                }
            }
        }
    }

}
