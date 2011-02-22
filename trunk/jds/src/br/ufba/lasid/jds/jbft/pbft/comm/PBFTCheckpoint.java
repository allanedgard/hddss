/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckpoint extends PBFTMessage{

    @Override
    public final synchronized String toString() {

        return (
                "<CHECKPOINT" + "," +
                 "SEQ = " + getSequenceNumber().toString() + ", " +
                 "SENDER = " + getReplicaID().toString() +
                 ">"
        );
    }

    Long sequenceNumber;

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    Object replicaID;

    public Object getReplicaID() {
        return replicaID;
    }

    public void setReplicaID(Object replicaID) {
        this.replicaID = replicaID;
    }
    

}
