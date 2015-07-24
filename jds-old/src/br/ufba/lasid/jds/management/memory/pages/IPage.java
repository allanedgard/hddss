/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.pages;

/**
 *
 * @author aliriosa
 */
public interface IPage {

    public long getSize();
    
    public byte[] getBytes() throws Exception;
    
    public long getIndex() throws Exception;
    
    public void setIndex(long index) throws Exception;

    public long getOffset() throws Exception;
    
    public void setOffset(long offset) throws Exception;

    public void setBytes(byte[] bytes, long length) throws Exception;

    public long getMaxSize() throws Exception;
    public void setMaxSize(long maxSize) throws Exception;

    public void setSize(long size) throws Exception;

}
