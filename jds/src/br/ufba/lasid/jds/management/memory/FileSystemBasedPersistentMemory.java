/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory;

import br.ufba.lasid.jds.management.JDSConfigurator;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.pages.PageIndexList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class FileSystemBasedPersistentMemory extends RandomAccessFile implements IPersistentMemory{
    Properties options = new Properties();
    PageIndexList modOffsets = new PageIndexList();
    
    public FileSystemBasedPersistentMemory(File file, String mode) throws FileNotFoundException {
        super(file, mode);
    }

    public FileSystemBasedPersistentMemory(String name, String mode) throws FileNotFoundException {
        super(name, mode);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try{
            return super.read(b, off, len);
        }catch(Exception except){
            throw (IOException)except;
        }
    }
    
    @Override
    public int read(byte[] buffer) throws IOException {
        return super.read(buffer);
    }

    public IPage readPage(long pageindex) throws Exception {
        
        
        long doffset = getPageOffset(pageindex);
        long size = getPageSize();

        long msize = getCurrentAllocatedSize();
        
        if(doffset + size > msize){
            size = msize - doffset;
        }
        
        if(size > 0){
            IPage page = JDSConfigurator.create(JDSConfigurator.PageProvider, options);
            page.setOffset(doffset);
            page.setIndex(pageindex);
            seek(doffset);
            size = this.read(page.getBytes(), 0, (int)size);
            page.setSize(size);
            return page;
        }
        
        return null;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try{
            long offset = getCurrentOffset();
            
            super.write(b, off, len);
            
            computModifiedPageIndexes(offset, len);

        }catch(Exception except){
            throw (IOException)except;
        }
    }

    @Override
    public void write(byte[] buffer) throws IOException {
        try{
            long offset = getCurrentOffset();

            super.write(buffer);
            
            computModifiedPageIndexes(offset, buffer.length);
            
        } catch (Exception ex) {
            throw (IOException) ex;
        }
    }

    public void writePage(IPage page) throws Exception {

        long offset = page.getOffset();

        seek(offset);

        write(page.getBytes(), 0, (int)page.getMaxSize());

    }

    @Override
    public void seek(long offset) throws IOException {
        super.seek(offset);
    }

    public long getCurrentOffset() throws Exception {
        return super.getFilePointer();
    }

    public long getCurrentAllocatedSize() throws Exception {
        return super.length();
    }

    public long getCurrentNumberOfPages() throws Exception {
        return getPageIndex(getCurrentAllocatedSize());
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
    public PageIndexList getRecentlyModifiedPageIndexes() throws Exception {

        Collections.sort(modOffsets);

        return modOffsets;
    }

    public void clearListOfModifiedPages() throws Exception {
        modOffsets.clear();
    }

    protected long pageSize = 4096;
    
    public long getPageSize() throws Exception {
        return this.pageSize;
    }

    public void setPageSize(long newPageSize) throws Exception {
        this.pageSize = newPageSize;
        options.put( JDSConfigurator.MaximumPageSize,
                     String.valueOf(this.pageSize));
    }

    public void setOptions(Properties options){
        this.options.putAll(options);
    }
    
    public void setPageFactory(String name){
        options.put(JDSConfigurator.PageProvider, name);
    }

    public long getPageOffset(long pageindex) throws Exception{
        return pageindex * getPageSize();
    }
    
    public long getPageIndex(long offset) throws Exception{
        return (long)Math.ceil(offset/getPageSize());
    }

    public long getOffsetInPage() throws Exception {
        return getCurrentOffset() % getPageSize();
    }

    public Properties getOptions() {
        return this.options;
    }

    public void release() throws Exception {
        this.close();
    }
}
