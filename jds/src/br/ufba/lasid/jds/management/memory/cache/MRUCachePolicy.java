/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.cache;

import br.ufba.lasid.jds.management.JDSConfigurator;
import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;

/**
 *
 * @author aliriosa
 */
public class MRUCachePolicy extends MRUStrategy<Long, IPage> implements ICachePolicy{

    IMemory memory;
    
    public MRUCachePolicy(int max){
        super(max);
    }

    protected int readPages(byte[] buffer, int bufferOffset, int length) throws Exception{
        
        long doffset = this.memory.getCurrentOffset();
        long endOffset  =  doffset + length;
        long psize = this.memory.getPageSize();
        long maxOffset = this.memory.getCurrentAllocatedSize();
        long bytes = length;

        if(endOffset > maxOffset){
            return -1;
        }
        
        while(bytes > 0){
            
            long pageindex = this.memory.getPageIndex(
                                this.memory.getCurrentOffset()
                             );

            long oip = this.memory.getOffsetInPage();

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
    public int read(byte[] buffer, int offset, int length) throws Exception {
        return readPages(buffer, offset, length);
    }

    public int read(byte[] buffer) throws Exception {
        return readPages(buffer, 0, buffer.length);
    }

    public IPage readPage(long pageindex) throws Exception {
        long npages = this.memory.getCurrentNumberOfPages();

        if(pageindex > npages){
            return null;
        }

        IPage page = get(pageindex);

        if(page == null){
            
            page = this.memory.readPage(pageindex);

            if(page != null){
                put(pageindex, page);
            }
        }

        return page;


    }

    public void setMainMemory(IMemory memory) throws Exception {
        this.memory = memory;
    }

    protected void writePages(byte[] buffer, int boffset, int length) throws Exception {
        
        long bytes = length;
        long psize = this.memory.getPageSize();


        while(bytes > 0){

            long pageindex = this.memory.getPageIndex(
                                this.memory.getCurrentOffset()
                             );
            long oip = this.memory.getOffsetInPage();
            
            IPage page = readPage(pageindex);
            
            if(page == null){
                page = JDSConfigurator.create(JDSConfigurator.PageProvider, this.memory.getOptions());
                page.setIndex(pageindex);
                page.setOffset(this.memory.getPageOffset(pageindex));
            }

            
            long tot = psize - oip;


            if(tot > bytes){
                tot = bytes;
            }

            System.arraycopy(buffer, boffset, page.getBytes(), (int)oip, (int)tot);

            writePage(page);
            
            bytes -= tot;
            boffset += tot;

        }
        
    }
    public void write(byte[] buffer, int offset, int length) throws Exception {
        writePages(buffer, offset, length);
    }

    public void write(byte[] buffer) throws Exception {
        writePages(buffer, 0, buffer.length);
    }

    public void writePage(IPage page) throws Exception {
        put(page.getIndex(), page);
        this.memory.writePage(page);
    }

    public void removePage(long pageIndex) throws Exception {
        remove(pageIndex);
    }

    public void removeAll() throws Exception {
        clear();
    }

    public void setMaximumCacheSize(int newSize) throws Exception {
        this.max = newSize;
    }

    public int getMaximumCacheSize() throws Exception {
        return this.max;
    }
}
