/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory;

import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.pages.PageIndexList;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public interface IMemory {
    /*#######################################################
     * 0. Reading methods.
     ########################################################*/
    /**
     * Reads up to length bytes of data from this memory into an array of bytes.
     * @param buffer - the buffer into which the data is read.
     * @param offset - the start offset in array buffer at which the data is written.
     * @param length - the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or -1 if there is
     * no more data because the end of allocated bytes in the memory has been
     * reached.
     * @throws Exception - if an I/O error occurs.
     */
     public int read(byte[] buffer, int offset, int length) throws Exception;

    /**
     * Reads up to buffer.length bytes of data from this memory into an array of
     * bytes.
     * @param buffer - the buffer into which the data is read.
     * @return the total number of bytes read into the buffer, or -1 if there is
     * no more data because the end of allocated bytes in the memory has been
     * reached.
     * @throws Exception - if an I/O error occurs.
     */
     public int read(byte[] buffer) throws Exception;

    /**
     * Reads up to <B>getPageSize()</B> bytes of data from this memory in an
     * page.
     * @param pageindex - the page index.
     * @return the read page with <B>page.size() </B> bytes, or null if there
     * is no more pages because the end of the allocated bytes in memory has
     * been reached.
     * @throws Exception - if an I/O error occurs.
     */
    public IPage readPage(long pageindex) throws Exception;


    /*#######################################################
     * 1. Writing methods.
     ########################################################*/
     /**
      * Writes length bytes from the specified buffer starting at offset to this
      * memory.
      * @param buffer - the data.
      * @param offset - the start offset in data.
      * @param length - the number of bytes to write.
      * @throws Exception - if an I/O error occurs.
      */
    public void write(byte[] buffer, int offset, int length) throws Exception;

    /**
     * Writes buffer.length bytes from the specified buffer to this memory.
     * @param buffer - the data.
     * @throws Exception - if an I/O error occurs.
     */
    public void write(byte[] buffer) throws Exception;

    /**
     * Writes <B>getPageSize()</B> bytes from the specified page starting at
     * <B>page.getOffset()</B> to this memory.
     * @param page - the page.
     * @throws Exception - if an I/O error occurs.
     */
    public void writePage(IPage page) throws Exception;

    /*#######################################################
     * 2. Navigation methods.
     ########################################################*/
    /**
     * Sets the memory-point offset, measured from begining of the this memory,
     * at which the next read or writePage occurs.
     * @param offset - the offset position, measured in bytes from the begining
     * of the memory, at which to set the memory pointer.
     * @throws Exception - if offset is less then 0 or if an I/O error occurs.
     */
    public void seek(long offset) throws Exception;

    /*#######################################################
     * 3. Utility methods.
     ########################################################*/

    /**
     * Returns the current offset in this memory.
     * @return the offset from the begining of the memory, in bytes, at which
     * the next read or writePage occurs.
     * @throws Exception - if an I/O error occurs.
     */
    public long getCurrentOffset() throws Exception;

    /**
     * Returns the current allocated size of this memory.
     * @return the current allocated size of this memory, measured in bytes.
     * @throws Exception - if an I/O error occurs.
     */
    public long getCurrentAllocatedSize() throws Exception;

    /**
     * Returns the current number of pages of this memory. It is measured using:
     * <P>
     * numberOfPages = Math.ceil(getCurrentAllocatedSize()/getPageSize());
     * </P>
     * @return the current number of pages of this memory.
     * @throws Exception - if an I/O error occurs.
     */
    public long getCurrentNumberOfPages() throws Exception;

    /**
     * Returns the maximum size, mensuared in bytes, of a memory page.
     * @return - the maximum size of a memory page.
     * @throws Exception - if an I/O error occurs.
     */
    public long getPageSize() throws Exception;

    /**
     * Sets the maximum size, mensuared in bytes, of a memory page.
     * @param newPageSize - the maximum size, mensuared in bytes, of a memory page
     * @throws Exception - if an I/O error occurs.
     */
    public void setPageSize(long newPageSize) throws Exception;

    /**
     * Returns a list of page indexes that were recently modified.
     * @return - the list of modified page indexes.
     * @throws Exception - if an I/O error occurs.
     */
    public PageIndexList getRecentlyModifiedPageIndexes() throws Exception;

    /**
     * Clears the current list of recently modified pages.
     * @throws Exception - if an I/O error occurs.
     */
    public void clearListOfModifiedPages() throws Exception;

    /**
     * Sets the length of this memory.
     * <P>
     * If the allocated size of the memory as returned by the
     * <I>getCurrentAllocatedSize</I> method is greater than <B>newSize</B>
     * argument then the allocated bytes of this memory will be truncated. In
     * this case, if the memory offset as returned by the <B>getCurrentOffset</B>
     * method is greather than newSize then after this method returns the offset
     * will be equal to <B>newSize</B>.
     * </P>
     * <P>
     * If the present allocated size of the memory as returned by the
     * <I>getCurrentAllocatedSize</I> method is smaller than <B>newSize</B>
     * argument then the size allocated will be extended. In this case, the
     * contents of the extended portion of the memory are not defined.
     * </P>
     * @param newSize - the desired size to be allocated in the memory, measured
     * in bytes.
     * @throws Exception - If an I/O error occurs.
     */
    public void setLength(long newSize) throws Exception;

    public void setOptions(Properties options);
    public Properties getOptions();

    public long getPageIndex(long offset) throws Exception;
    public long getOffsetInPage() throws Exception;

    public long getPageOffset(long pageindex) throws Exception;

    public void release() throws Exception;
}
