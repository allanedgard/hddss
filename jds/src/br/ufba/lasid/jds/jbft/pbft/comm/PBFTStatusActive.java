/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.util.DigestList;
import java.util.BitSet;

/**
 *
 * @author aliriosa
 */
public class PBFTStatusActive extends PBFTServerMessage{

    /**
     * Represents the sequence number of the last stable checkpoint
     */
    protected Long lastStableCheckpointSEQ;
    /**
     * Represents the sequence number of the last prepared request(s)
     */
    protected Long lastPrePreparedSEQ;

    /**
     * Represents the sequence number of the last prepared request(s)
     */
    protected Long lastPreparedSEQ;

    /**
     * Represents the sequence number of the last commited request(s).
     */
    protected Long lastCommittedSEQ;

    /**
     * Represents the sequence number of the last executed request.
     * We use the sequence number of the last executed pre-prepare because, when
     * the batch strategy is used, a pre-prepare can be related to a set of 
     * client request.
     */
    protected Long lastExecutedSEQ;

    protected DigestList missedRequests = new DigestList();

    protected BitSet prepared = new BitSet();
    protected BitSet commited = new BitSet();

   public BitSet getCommited() {
      return commited;
   }

   public BitSet getPrepared() {
      return prepared;
   }

    public Long getLastStableCheckpointSEQ() {
        return lastStableCheckpointSEQ;
    }

    public void setLastStableCheckpointSEQ(Long lastStableCheckpointSEQ) {
        this.lastStableCheckpointSEQ = lastStableCheckpointSEQ;
    }

//    public Long getLastCommittedSEQ() {
//        return lastCommittedSEQ;
//    }
//
//    public void setLastCommittedSEQ(Long lastCommittedSEQ) {
//        this.lastCommittedSEQ = lastCommittedSEQ;
//    }

    public Long getLastExecutedSEQ() {
        return lastExecutedSEQ;
    }

    public void setLastExecutedSEQ(Long lastExecutedSEQ) {
        this.lastExecutedSEQ = lastExecutedSEQ;
    }

//    public Long getLastPreparedSEQ() {
//        return lastPreparedSEQ;
//    }
//
//    public void setLastPreparedSEQ(Long lastPreparedSEQ) {
//        this.lastPreparedSEQ = lastPreparedSEQ;
//    }
//    public Long getLastPrePreparedSEQ() {
//        return lastPrePreparedSEQ;
//    }
//
//    public void setLastPrePreparedSEQ(Long lastPrePreparedSEQ) {
//        this.lastPrePreparedSEQ = lastPrePreparedSEQ;
//    }

   public DigestList getMissedRequests() {
      return missedRequests;
   }

   private String missedRequestsToString(){
      String str = "";
      String more = "";

      for(String digest : getMissedRequests()){
         str += more + digest;
         more = ",";
      }
      return str;
   }


    public PBFTStatusActive(Long bseqn, Object replicaID, Integer viewNumber, Long executedSEQ, Long checkpointSEQ){
        setViewNumber(viewNumber);
//        setLastPrePreparedSEQ(prepreparedSEQ);
//        setLastPreparedSEQ(preparedSEQ);
//        setLastCommittedSEQ(committedSEQ);
        setLastExecutedSEQ(executedSEQ);
        setLastStableCheckpointSEQ(checkpointSEQ);
        setReplicaID(replicaID);
        setSequenceNumber(bseqn);
    }

    private String preparedToString(){
       String str = "";

       for(int b = 0; b < prepared.length(); b++){
          boolean bit = prepared.get(b);
          str += bit ? "1" : "0";
       }
       return str;
    }

    private String commitedToString(){
       String str = "";

       for(int b = 0; b < commited.length(); b++){
          boolean bit = commited.get(b);
          str += bit ? "1" : "0";
       }
       return str;
    }

    @Override
    public String toString() {
        return (
                "<STATUS-ACTIVE" + ", " +
                 "BSEQN = " + getSequenceNumber() + ", " + 
                 "VIEW = " + getViewNumber().toString()     + ", " +
                 "STABLECHECKPOINTSEQ = " + getLastStableCheckpointSEQ().toString()  + ", " +
                 "EXECUTEDSEQ = " + getLastExecutedSEQ().toString()  + ", " +
                 "PREPARED = " + preparedToString() + ", " +
                 "COMMITED = " + commitedToString() + ", " + 
                 "MISSED-REQUESTS = {" + missedRequestsToString() + "}, " +
                 "SERVER = " + getReplicaID().toString() +
                 ">"
        );
    }

   public int getTAG() {
      return IPBFTServer.STATUSACTIVE;
   }

   public String getTAGString() {
      return "STATUSACTIVE";
   }

}
