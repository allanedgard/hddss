/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.Quorum;
import trash.br.ufba.lasid.jds.comm.QuorumTable;
import trash.br.ufba.lasid.jds.comm.QuorumTableStore;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.util.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.util.StatedPBFTRequestMessage.RequestState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class PBFTStateLog extends Hashtable<Long, PBFTLogEntry>{

    volatile QuorumTableStore qstore = new QuorumTableStore();
    
    private static String PREPAREQUORUMTABLE = "__PREPAREQUORUMTABLE";
    private static String COMMITQUORUMTABLE = "__COMMITQUORUMTABLE";
    private static String CHECKPOINTQUORUMTABLE = "__CHECKPOINTQUORUMTABLE";
    private static String REPLYQUORUMTABLE = "__REPLYQUORUMTABLE";
   
    ArrayList<Long> nsequences = new ArrayList<Long>();
    
    private int lastChangeViewTimestamp = -1;

    private long cpLowWaterMark = -1;

    private volatile PBFTRequestTable rseqtable = new PBFTRequestTable();
    private volatile PBFTRequestTable rdigtable = new PBFTRequestTable();

    public boolean wasPrePrepared(PBFTPrepare p){
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
        
        return false;
    }

    public boolean wasPrepared(PBFTCommit c){
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

        return false;
    }
    
    public synchronized void insertRequestInTables(String digest, PBFTRequest r, RequestState rstate){

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
        
        PBFTLogEntry entry = get(seqn);

        if(entry != null){
            return entry.getPrepareQuorum();
        }

        return null;
    }

    public Quorum getCommitQuorum(Long seqn){

        PBFTLogEntry entry = get(seqn);

        if(entry != null){
            return entry.getCommitQuorum();
        }

        return null;
    }

    public PBFTPrePrepare getPrePrepare(Long seqn){
        
        PBFTLogEntry entry = get(seqn);

        if(entry != null){
            return entry.getPrePrepare();
        }

        return null;

    }

    public void setCheckpointLowWaterMark(long mark){
        cpLowWaterMark = mark;
    }
    
    public long getCheckpointLowWaterMark(){
        return cpLowWaterMark;
    }

    public long getCheckpointHighWaterMark(long checkpointPeriod, long factor){
        return cpLowWaterMark + factor * checkpointPeriod;
    }

    public void putInBacklog(PBFTRequest req){
        changeRequestStatus(req);
    }

    public PBFTReply getReplyInRequestTable(PBFTRequest r){
        return getReplyInRequestTable(r.getClientID(), r.getTimestamp());
    }

    public PBFTReply getReplyInRequestTable(Object clientID, Long timestamp){

        try{

            PBFTRequestTuple rtuple = rseqtable.get(clientID);
            StatedPBFTRequestMessage stateReq = rtuple.get(timestamp);
            return stateReq.getReply();

        }catch(Exception ex){
            ex.printStackTrace();
            return null;

        }

    }
    
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

    public boolean wasAlreadyAccepted(PBFTRequest r){

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

        QuorumTable qtable = qstore.get(name);

        if(qtable == null){

            qtable = new QuorumTable();
            qstore.put(name, qtable);
        }

        return qtable;
        
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
    public synchronized boolean noMore(PBFTRequest request) {

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

    protected boolean hasLowestTimestamp(PBFTRequest r){

        try{

            PBFTRequestTuple rtuple = rseqtable.get(r.getClientID());
            
            ArrayList<Long> myTimestamps = new ArrayList<Long>(rtuple.keySet());

            Collections.sort(myTimestamps);

            long maxTimestamp = myTimestamps.get(myTimestamps.size()-1);
            long theTimestamp = r.getTimestamp();

            return maxTimestamp > theTimestamp;


        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public long getLastExecutedSequenceNumber(){

        /**
         * It isn't efficient.
         */
        Collections.sort(nsequences);

        if(nsequences.isEmpty()){
            return -1;
        }

        return nsequences.get(nsequences.size()-1);
        
    }

    public synchronized  void cleanupStoredSequence(long seqn){

        if(!nsequences.isEmpty()){

            ArrayList<Long> sequences = new ArrayList<Long>();

            sequences.addAll(nsequences);

            Collections.sort(sequences);

            long max = sequences.get(sequences.size()-1);

            for(long seq : sequences){
                
                if(seq <= seqn && seq < max){
                    nsequences.remove(seq);
                }
            }
        }
        
    }

}
