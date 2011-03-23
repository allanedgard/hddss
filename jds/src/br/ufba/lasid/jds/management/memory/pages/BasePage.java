/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.pages;

import java.io.Serializable;

/**
 *
 * @author aliriosa
 */
public class BasePage implements IPage, Serializable{
    protected long size = 0;
    protected long offset;
    protected long index;
    protected long maxPageSize;
    protected byte[] bytes;
    
    public BasePage(long maxPageSize){
        this.maxPageSize = maxPageSize;
        bytes = new byte[(int)maxPageSize];
    }
    public long getSize() {
        return this.size;
    }

    public byte[] getBytes() throws Exception {
        return this.bytes;
    }

    public long getIndex() throws Exception {
        return this.index;
    }

    public void setIndex(long index) throws Exception {
        this.index = index;
    }

    public long getOffset() throws Exception {
        return this.offset;
    }

    public void setOffset(long offset) throws Exception {
        this.offset = offset;
    }

    public void setBytes(byte[] bytes, long length) throws Exception {
        System.arraycopy(bytes, 0, this.bytes, 0, (int)length);
        this.size = length;
    }

    public long getMaxSize() throws Exception {
        return this.maxPageSize;
    }

    public void setMaxSize(long maxSize) throws Exception {
        this.maxPageSize = maxSize;
    }

    public void setSize(long size) throws Exception{
        this.size = size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BasePage other = (BasePage) obj;
        if (this.size != other.size) {
            return false;
        }
        if (this.offset != other.offset) {
            return false;
        }
        if (this.index != other.index) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.size ^ (this.size >>> 32));
        hash = 53 * hash + (int) (this.offset ^ (this.offset >>> 32));
        hash = 53 * hash + (int) (this.index ^ (this.index >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "BasePage{" + "size=" + size + ", offset=" + offset + ", index=" + index + ", maxSize=" + maxPageSize + ", bytes=" + new String(bytes) + '}';
    }



}
