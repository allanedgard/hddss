/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.cache;

import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.pages.PageIndexList;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class Cache implements ICache{
    protected IMemory memory;
    protected ICachePolicy policy;
    
    public Cache(IMemory memory, ICachePolicy policy) throws Exception{
        this.memory = memory;
        this.policy = policy;
        this.policy.setMainMemory(memory);
    }
    
    public void setMaxSize(int newSize) throws Exception {
        this.policy.setMaximumCacheSize(newSize);
    }

    public int getMaxSize() throws Exception {
        return this.policy.getMaximumCacheSize();
    }

    public void setPolicy(ICachePolicy policy) throws Exception {
        if(this.policy == null){
            this.policy = policy;
        }
    }

    public ICachePolicy getPolicy() throws Exception {
        return this.policy;
    }

    public int read(byte[] buffer, int offset, int length) throws Exception {
        return this.policy.read(buffer, offset, length);
    }

    public int read(byte[] buffer) throws Exception {
        return this.policy.read(buffer);
    }

    public IPage readPage(long pageindex) throws Exception {
        return this.policy.readPage(pageindex);
    }

    public void write(byte[] buffer, int offset, int length) throws Exception {
        this.policy.write(buffer, offset, length);
    }

    public void write(byte[] buffer) throws Exception {
        this.policy.write(buffer);
    }

    public void writePage(IPage page) throws Exception {
        this.policy.writePage(page);
    }

    public void seek(long offset) throws Exception {
        this.memory.seek(offset);
    }

    public long getCurrentOffset() throws Exception {
        return this.memory.getCurrentOffset();
    }

    public long getCurrentAllocatedSize() throws Exception {
        return this.memory.getCurrentAllocatedSize();
    }

    public long getCurrentNumberOfPages() throws Exception {
        return this.memory.getCurrentNumberOfPages();
    }

    public long getPageSize() throws Exception {
        return this.memory.getPageSize();
    }

    public void setPageSize(long newPageSize) throws Exception {
        this.memory.setPageSize(newPageSize);
    }

    public PageIndexList getRecentlyModifiedPageIndexes() throws Exception {
        return this.memory.getRecentlyModifiedPageIndexes();
    }

    public void clearListOfModifiedPages() throws Exception {
        this.memory.clearListOfModifiedPages();
    }

    public void setLength(long newSize) throws Exception {
        this.memory.setLength(newSize);
    }

    public void setOptions(Properties options) {
        this.memory.setOptions(options);
    }

    public long getPageIndex(long offset) throws Exception {
        return this.memory.getPageIndex(offset);
    }

    public long getOffsetInPage() throws Exception {
        return this.memory.getOffsetInPage();
    }

    public Properties getOptions() {
        return this.memory.getOptions();
    }

    public long getPageOffset(long pageindex) throws Exception {
        return this.memory.getPageOffset(pageindex);
    }

    public void release() throws Exception {
        this.memory.release();
    }


}
