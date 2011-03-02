/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util.trash;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class Partition implements Serializable{
    
    private static final long serialVersionUID = 7310514093659572809L;

    protected String digest;
    protected int capacity = 256;
    protected long sequenceNumber;
    protected transient Partition parent = null;

    ArrayList<Partition> subpartitions;

    public Partition(int capacity){
        subpartitions = new ArrayList<Partition>(capacity);
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Partition getParent() {
        return parent;
    }

    public void setParent(Partition parent) {
        this.parent = parent;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Partition getSubpartition(int index) {
        return subpartitions.get(index);
    }

    public void addSubpartition(Partition child) {
        if(this.getCapacity() > this.subpartitions.size())
            this.subpartitions.add(child);
    }

    public boolean isRoot(){
        return this.parent == null;
    }

    public boolean isFull(){
        return this.subpartitions.size() == this.capacity;
    }
}
