/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTServerMessage extends PBFTMessage{
    
    protected Integer viewNumber;

    public Integer getViewNumber() {
        return viewNumber;
    }

    public void setViewNumber(Integer viewNumber) {
        this.viewNumber = viewNumber;
    }

    protected Long sequenceNumber;

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    protected Object replicaID;

    public Object getReplicaID() {
        return replicaID;
    }

    public void setReplicaID(Object replicaID) {
        this.replicaID = replicaID;
    }


}
