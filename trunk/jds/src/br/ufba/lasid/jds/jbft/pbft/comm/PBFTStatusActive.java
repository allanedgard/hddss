/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

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

    public Long getLastStableCheckpointSEQ() {
        return lastStableCheckpointSEQ;
    }

    public void setLastStableCheckpointSEQ(Long lastStableCheckpointSEQ) {
        this.lastStableCheckpointSEQ = lastStableCheckpointSEQ;
    }

    public Long getLastCommittedSEQ() {
        return lastCommittedSEQ;
    }

    public void setLastCommittedSEQ(Long lastCommittedSEQ) {
        this.lastCommittedSEQ = lastCommittedSEQ;
    }

    public Long getLastExecutedSEQ() {
        return lastExecutedSEQ;
    }

    public void setLastExecutedSEQ(Long lastExecutedSEQ) {
        this.lastExecutedSEQ = lastExecutedSEQ;
    }

    public Long getLastPreparedSEQ() {
        return lastPreparedSEQ;
    }

    public void setLastPreparedSEQ(Long lastPreparedSEQ) {
        this.lastPreparedSEQ = lastPreparedSEQ;
    }
    public Long getLastPrePreparedSEQ() {
        return lastPrePreparedSEQ;
    }

    public void setLastPrePreparedSEQ(Long lastPrePreparedSEQ) {
        this.lastPrePreparedSEQ = lastPrePreparedSEQ;
    }


    public PBFTStatusActive(Object replicaID, Integer viewNumber, Long prepreparedSEQ, Long preparedSEQ, Long committedSEQ, Long executedSEQ, Long checkpointSEQ){
        setViewNumber(viewNumber);
        setLastPrePreparedSEQ(prepreparedSEQ);
        setLastPreparedSEQ(preparedSEQ);
        setLastCommittedSEQ(committedSEQ);
        setLastExecutedSEQ(executedSEQ);
        setLastStableCheckpointSEQ(checkpointSEQ);
        setReplicaID(replicaID);
    }
    
    @Override
    public String toString() {
        return (
                "<STATUS-ACTIVE" + ", " +
                 "VIEW = " + getViewNumber().toString()     + ", " +
                 "STABLECHECKPOINTSEQ = " + getLastStableCheckpointSEQ().toString()  + ", " +
                 "PREPREPAREDSEQ = " + getLastPrePreparedSEQ().toString()  + ", " +
                 "PREPAREDSEQ = " + getLastPreparedSEQ().toString()  + ", " +
                 "COMMITEDSEQ = " + getLastCommittedSEQ().toString()  + ", " +
                 "EXECUTEDSEQ = " + getLastExecutedSEQ().toString()  + ", " +
                 "SERVER = " + getReplicaID().toString()    + 
                 ">"
        );
    }

}
