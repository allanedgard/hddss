/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory;

import br.ufba.lasid.jds.util.JDSUtility;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.pages.PageIndexList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class BufferMemory  implements IVolatileMemory{
    protected int pos;
    protected byte[] buf;
    protected Properties options = new Properties();
    protected PageIndexList modOffsets = new PageIndexList();
    
    public BufferMemory() {
        buf = new byte[256];
    }

    public BufferMemory(byte[] buf){
        this.buf = buf;
    }

    protected long pageSize = 4096;

    public long getPageSize() throws Exception {
        return this.pageSize;
    }

    public void setPageSize(long newPageSize) throws Exception {
        this.pageSize = newPageSize;
        options.put( JDSUtility.MaximumPageSize,
                     String.valueOf(this.pageSize));
    }

    public void setOptions(Properties options){
        this.options.putAll(options);
    }

    public void setPageFactory(String name){
        options.put(JDSUtility.PageProvider, name);
    }

    public long getPageOffset(long pageindex) throws Exception{
        return pageindex * getPageSize();
    }

    public long getPageIndex(long offset) throws Exception{
        return (long)Math.ceil((double)offset/(double)getPageSize());
    }

    public long getOffsetInPage() throws Exception {
        return getCurrentOffset() % getPageSize();
    }

    public Properties getOptions() {
        return this.options;
    }

    public int read(byte[] buffer, int off, int len) throws Exception {
	if (buffer == null) {
	    throw new NullPointerException();
	} else if (off < 0 || len < 0 || len > buffer.length - off) {
	    throw new IndexOutOfBoundsException();
	} else if (len == 0) {
	    return 0;
	}

	int avail = buf.length - pos;
	if (avail > 0) {
	    if (len < avail) {
		avail = len;
	    }
	    System.arraycopy(buf, pos, buffer, off, avail);
	    pos += avail;
	}

        return avail;
    }

    public int read(byte[] buffer) throws Exception {
	if (buffer == null) {
	    throw new NullPointerException();
        }

        return read(buffer, 0, buffer.length);
    }

    public IPage readPage(long pageindex) throws Exception {
        long doffset = getPageOffset(pageindex);
        long size = getPageSize();

        long msize = getCurrentAllocatedSize();

        if(doffset + size > msize){
            size = msize - doffset;
        }

        if(size > 0){
            IPage page = JDSUtility.create(JDSUtility.PageProvider, options);
            page.setOffset(doffset);
            page.setIndex(pageindex);
            seek(doffset);
            size = this.read(page.getBytes(), 0, (int)size);
            page.setSize(size);
            return page;
        }

        return null;
    }

    public void write(byte[] b, int off, int len) throws Exception {
        
	if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) > b.length) || ((off + len) < 0)) {
	    throw new IndexOutOfBoundsException();
	} else if (len == 0) {
	    return;
	}

        long offset = getCurrentOffset();

        int newpos = pos + len;
        if (newpos > buf.length) {
            buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newpos));
        }
        System.arraycopy(b, off, buf, pos, len);
        pos = newpos;

        computModifiedPageIndexes(offset, len);


    }


    public void graft(byte[] b) throws Exception{
        graft(b, 0, b.length);
    }
    public void graft(byte[] b, int off, int len) throws Exception{
	if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) > b.length) || ((off + len) < 0)) {
	    throw new IndexOutOfBoundsException();
	} else if (len == 0) {
	    return;
	}

        int newlen = buf.length + len;

        if(pos < 0) pos = 0;
        if(pos > buf.length) pos = buf.length;


        int newpos = pos + len;
        
        byte[] newbuf = new byte[newlen];

        int remain = buf.length - pos;

        if(pos > 0){
            System.arraycopy(buf, 0, newbuf, 0, pos);
        }
        
        System.arraycopy(b, off, newbuf, pos, len);

        if(remain > 0){
            System.arraycopy(buf, pos, newbuf, newpos, remain);
        }

        computModifiedPageIndexes((long)pos, len + remain);

        pos = newpos;
        buf = newbuf;
        newbuf = null;

    }

    public void write(byte[] buffer) throws Exception {
        write(buffer, 0, buffer.length);
    }

    public void writePage(IPage page) throws Exception {
        long offset = page.getOffset();
        
        if(getCurrentAllocatedSize() <= page.getOffset()){
            setLength(page.getOffset() + page.getMaxSize());
        }

        seek(offset);

        write(page.getBytes(), 0, (int)page.getMaxSize());
    }

    public void seek(long offset) throws Exception {
        pos = (int)offset;
    }

    public long getCurrentOffset() throws Exception {
        return pos;
    }

    public long getCurrentAllocatedSize() throws Exception {
        return buf.length;
    }

    public long getCurrentNumberOfPages() throws Exception {
        return getPageIndex(getCurrentAllocatedSize());
    }


    public PageIndexList getRecentlyModifiedPageIndexes() throws Exception {
        Collections.sort(modOffsets);

        return modOffsets;
    }

    public void clearListOfModifiedPages() throws Exception {
        modOffsets.clear();
    }

    public void setLength(long newSize) throws Exception {
        if(newSize > 0){
            if(newSize > buf.length){
                pos = (int)newSize;
            }
            buf = Arrays.copyOf(buf, (int)newSize);
        }
    }


    public void release() throws Exception {
        
    }
    public void computModifiedPageIndexes(long offset, long length) throws Exception{

        long mark  = offset + length;
        long psize = getPageSize();

        for(long i = offset; i < mark; i += psize){
            long pageindex = getPageIndex(i);
            if(!modOffsets.contains(pageindex))
                modOffsets.add(pageindex);
        }

    }

    protected int readPages(byte[] buffer, int bufferOffset, int length) throws Exception{

        long doffset = getCurrentOffset();
        long endOffset  =  doffset + length;
        long psize = getPageSize();
        long maxOffset = getCurrentAllocatedSize();
        long bytes = length;

        if(endOffset > maxOffset){
            return -1;
        }

        while(bytes > 0){

            long pageindex = getPageIndex(
                                getCurrentOffset()
                             );

            long oip = getOffsetInPage();

            IPage page = readPage(pageindex);

            long tot = psize - oip;

            if(tot > bytes){
                tot = bytes;
            }

            System.arraycopy(page.getBytes(), (int)oip, buffer, bufferOffset, (int)tot);

            bytes -= tot;
            bufferOffset += tot;

        }

        return length;

    }

    public void writePages(byte[] buffer, int offset, int length) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
