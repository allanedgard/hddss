/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import java.io.Serializable;

/**
 *
 * @author aliriosa
 */
public class StateChunk implements Serializable{

    private static final long serialVersionUID = 107753151630658478L;
    
    private long offset = 0;
    private long length = 0;

    public StateChunk(long offset, long length) {
        setOffset(offset);
        setLength(length);
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StateChunk other = (StateChunk) obj;
        if (this.offset != other.offset) {
            return false;
        }
        if (this.length != other.length) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (int) (this.offset ^ (this.offset >>> 32));
        hash = 73 * hash + (int) (this.length ^ (this.length >>> 32));
        return hash;
    }


}
