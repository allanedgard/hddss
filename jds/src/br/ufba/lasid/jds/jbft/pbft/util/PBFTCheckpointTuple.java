/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IState;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckpointTuple {

    public Long seqn;
    public String digest;
    public IState currentState;
    public boolean updated = false;

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    

    public String getEntry(){
        return  seqn.toString() + ";" + digest.toString() ;
    }

    public PBFTCheckpointTuple(Long seqn, String digest, IState currentState, boolean updated) {
        this.seqn = seqn;
        this.digest = digest;
        this.currentState = currentState;
        this.updated = updated;
    }
    
    public PBFTCheckpointTuple(Long seqn, String digest, IState currentState) {
        this(seqn, digest, currentState, false);
    }
    
    public IState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(IState currentState) {
        this.currentState = currentState;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Long getSequenceNumber() {
        return seqn;
    }

    public void setSequenceNumber(Long seqn) {
        this.seqn = seqn;
    }    

}
