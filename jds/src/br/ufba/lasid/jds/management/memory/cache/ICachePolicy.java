/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.cache;

import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;

/**
 *
 * @author aliriosa
 */
public interface ICachePolicy {

    public int read(byte[] buffer, int offset, int length) throws Exception;
    public int read(byte[] buffer) throws Exception;
    public IPage readPage(long pageindex) throws Exception;
    public void setMainMemory(IMemory memory) throws Exception;
    public void write(byte[] buffer, int offset, int length) throws Exception;
    public void write(byte[] buffer) throws Exception;
    public void writePage(IPage page) throws Exception;

    public void removePage(long pageIndex) throws Exception;
    public void removeAll() throws Exception;

    public void setMaximumCacheSize(int newSize) throws Exception;
    public int  getMaximumCacheSize() throws Exception;

}
