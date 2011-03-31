/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckpoint extends PBFTServerMessage{

    public PBFTCheckpoint(){

    }

    public PBFTCheckpoint(Long seqn, String digest, Object replicaID){
        setSequenceNumber(seqn);
        setDigest(digest);
        setReplicaID(replicaID);        
    }
    @Override
    public final String toString() {
        Object rid = getReplicaID();
        return (
                "<CHECKPOINT" + ", " +
                 "SEQ = " + getSequenceNumber().toString() + ", " +
                 "DIGEST = " + getDigest().toString() + ", " +
                 "SENDER = " + (rid == null ? "NULL" : rid) +
                 ">"
        );
    }

    String digest;

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

}
