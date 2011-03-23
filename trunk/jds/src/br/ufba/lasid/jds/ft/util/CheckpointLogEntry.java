/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.ft.util;

import java.io.Serializable;

/**
 *
 * @author aliriosa
 */
public class CheckpointLogEntry implements Serializable{

    private static final long serialVersionUID = 6638519520159263608L;
    
    long   checkpointID = -1;
    byte[] state = null;
    String digest = "";
    boolean processed = false;

    public CheckpointLogEntry(long checkpointID, byte[] state) {
        this(checkpointID, state, "");
    }

    public CheckpointLogEntry(long checkpointID, byte[] state, String digest) {
        this(checkpointID, state, digest, false);
    }
    
    public CheckpointLogEntry(long checkpointID, byte[] state, String digest, boolean processed) {
        this.checkpointID = checkpointID;
               this.state = state;
              this.digest = digest;
           this.processed = processed;
    }
    
    public long getCheckpointID() {
        return checkpointID;
    }

    public void setCheckpointID(long checkpointID) {
        this.checkpointID = checkpointID;
    }

    public byte[] getState() {
        return state;
    }

    public void setState(byte[] state) {
        this.state = state;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public boolean wasProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    
}
