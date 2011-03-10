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

    protected Long lastStableCheckpointSequenceNumber = -1L;
    protected Long wantedCheckpointSequenceNumber     = -1L;
    protected Object selectedReplierID;

    public Long getLastStableCheckpointSequenceNumber() {
        return lastStableCheckpointSequenceNumber;
    }

    public void setLastStableCheckpointSequenceNumber(Long lastStableCheckpointSequenceNumber) {
        this.lastStableCheckpointSequenceNumber = lastStableCheckpointSequenceNumber;
    }

    public Object getSelectedReplierID() {
        return selectedReplierID;
    }

    public void setSelectedReplierID(Object selectedReplierID) {
        this.selectedReplierID = selectedReplierID;
    }

    public Long getWantedCheckpointSequenceNumber() {
        return wantedCheckpointSequenceNumber;
    }

    public void setWantedCheckpointSequenceNumber(Long wantedCheckpointSequenceNumber) {
        this.wantedCheckpointSequenceNumber = wantedCheckpointSequenceNumber;
    }

    public PBFTFetch(Long lc, Long c, Object replierID, Object replicaID){
        setLastStableCheckpointSequenceNumber(lc);
        setWantedCheckpointSequenceNumber(c);
        setSelectedReplierID(replierID);
        setReplicaID(replicaID);
    }

    public PBFTFetch(Long lc,  Object replierID, Object replicaID){
        setLastStableCheckpointSequenceNumber(lc);
        setSelectedReplierID(replierID);
        setReplicaID(replicaID);
    }

    @Override
    public final String toString() {
        String wanted = "nil";
        if(getWantedCheckpointSequenceNumber()!= null){
            wanted = getWantedCheckpointSequenceNumber().toString();
        }

        return (
                "<FETCH" + ", " +
                 "LC = " + getLastStableCheckpointSequenceNumber().toString() + ", " +
                 "C  = " + wanted + ", " +
                 "REPLIER = " + getSelectedReplierID().toString() + ", " +
                 "SENDER = " + getReplicaID().toString() + ", " + 
                 ">"
        );
    }
}
