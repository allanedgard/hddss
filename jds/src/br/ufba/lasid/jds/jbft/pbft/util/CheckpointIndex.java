/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import java.io.Serializable;

/**
 *
 * @author aliriosa
 */
public class CheckpointIndex  implements Serializable{

    private static final long serialVersionUID = 6569075473379896298L;

    protected Long sequenceNumber;
    protected String digest;

    public CheckpointIndex() {
    }

    
    public CheckpointIndex(Long sequenceNumber, String digest) {
        this.sequenceNumber = sequenceNumber;
        this.digest = digest;
    }

    public CheckpointIndex(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public CheckpointIndex(String pattern) {
        setIndexPattern(pattern);
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setIndexPattern(String pattern){
        String[] pair = pattern.split(";");
        setSequenceNumber(Long.valueOf(pair[0]));
        setDigest(pair[1]);        
    }
    
    public String getIndexPattern(){
        String seqn = "null";
        String dgst = "null";

        if(sequenceNumber != null) seqn = sequenceNumber.toString();
        if(digest != null) dgst = digest;

        return seqn + ";" + dgst;

    }

    @Override
    public String toString() {
        return "CheckpointIndex{sequenceNumber=" + sequenceNumber + "; digest=" + digest + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final CheckpointIndex other = (CheckpointIndex) obj;

        if (this.sequenceNumber != other.sequenceNumber && (this.sequenceNumber == null || !this.sequenceNumber.equals(other.sequenceNumber))) {
            return false;
        }
        if ((this.digest == null) ? (other.digest != null) : !this.digest.equals(other.digest)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.sequenceNumber != null ? this.sequenceNumber.hashCode() : 0);
        hash = 97 * hash + (this.digest != null ? this.digest.hashCode() : 0);
        return hash;
    }

    
    

}
