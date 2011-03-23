/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTFetch extends PBFTServerMessage{

    protected Long lcSEQ = -1L;
    protected Long partCheckpoint     = -1L;
    protected Long partIndex = 0L;
    protected Long partLevel = 0L;
    protected Object replier = null;

    public Long getLastStableCheckpointSequenceNumber() {
        return lcSEQ;
    }

    public void setLastStableCheckpointSequenceNumber(Long lcSEQ) {
        this.lcSEQ = lcSEQ;
    }

    public Object getSelectedReplierID() {
        return replier;
    }

    public void setSelectedReplierID(Object replier) {
        this.replier = replier;
    }

    public Long getPartCheckpoint() {
        return partCheckpoint;
    }

    public void setPartCheckpoint(Long cpart) {
        this.partCheckpoint = cpart;
    }

    public Long getPartitionIndex() {
        return partIndex;
    }

    public void setPartitionIndex(Long ipart) {
        this.partIndex = ipart;
    }

    public Long getPartLevel() {
        return partLevel;
    }

    public void setPartLevel(Long lpart) {
        this.partLevel = lpart;
    }

    public PBFTFetch(Long lpart, Long ipart, Long cpart, Long lcSEQ, Object replierID, Object replicaID){
        setPartLevel(lpart);
        setPartitionIndex(ipart);
        setLastStableCheckpointSequenceNumber(lcSEQ);
        setPartCheckpoint(cpart);
        setSelectedReplierID(replierID);
        setReplicaID(replicaID);
    }

    public PBFTFetch(Long lc,  Object replierID, Object replicaID){
        setLastStableCheckpointSequenceNumber(lc);
        setSelectedReplierID(replierID);
        setReplicaID(replicaID);
    }
    

//    public PBFTFetch(Long lc, Long c, Object replierID, Object replicaID){
//        setLastStableCheckpointSequenceNumber(lc);
//        setPartCheckpoint(c);
//        setSelectedReplierID(replierID);
//        setReplicaID(replicaID);
//    }
//
//    public PBFTFetch(Long lc,  Object replierID, Object replicaID){
//        setLastStableCheckpointSequenceNumber(lc);
//        setSelectedReplierID(replierID);
//        setReplicaID(replicaID);
//    }

    @Override
    public final String toString() {
        String wanted = "nil";
        if(getPartCheckpoint()!= null){
            wanted = getPartCheckpoint().toString();
        }

        return (
                "<FETCH" + ", " +
                 "L = " + getPartLevel() + ", " +
                 "X = " + getPartitionIndex() + ", " +
                 "LC = " + getLastStableCheckpointSequenceNumber() + ", " +
                 "C  = " + wanted + ", " +
                 "REPLIER = " + getSelectedReplierID() + ", " +
                 "SENDER = " + getReplicaID() + ", " + 
                 ">"
        );
    }
}
