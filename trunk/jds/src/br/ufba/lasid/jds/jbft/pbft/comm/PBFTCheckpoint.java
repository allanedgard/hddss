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
    public final synchronized String toString() {

        return (
                "<CHECKPOINT" + ", " +
                 "SEQ = " + getSequenceNumber().toString() + ", " +
                 "DIGEST = " + getDigest().toString() + ", " +
                 "SENDER = " + getReplicaID().toString() +
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

    public String getEntryKey(){
        return  getSequenceNumber().toString() + ";" + getDigest().toString();
    }

}
