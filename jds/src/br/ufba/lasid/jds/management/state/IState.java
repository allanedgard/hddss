/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.pages.PageIndexList;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public interface IState <VariableID, VariableValue>{

    /**
     * Gets a variable value from the current state, using a variable id.
     * This method can be use to retrive short variables from this state.
     * @param variableID - the variable identifier.
     * @return - the variable value or null if it doesn't exist.
     * @throws Exception - if an error occurs.
     */
    public VariableValue get(VariableID variableID) throws Exception;

    /**
     * Adds or updates a variable in current state. This method can be use
     * to allocate or update short variables in the current state.
     * @param variableID - the variable identifier.
     * @param variableValue - the variable value.
     * @throws Exception -  if an error occurs.
     */
    public void put(VariableID variableID, VariableValue variableValue) throws Exception;

    /**
     * Removes a variable value from the current state, using a variable id.
     * @param variableID - the variable identifier.
     * @throws Exception - if an error occurs.
     */
    public void remove(VariableID variableID) throws Exception;


    /**
     * Writes length bytes from the specified buffer starting at offset to this
     * state. This method can be use to write a large amount of bytes in current
     * state.
     * @param buffer - the data.
     * @param offset - the start offset in data.
     * @param length - the number of bytes to write.
     * @throws Exception - if an error occurs.
     */
    public void write(byte[] buffer, int offset, int length) throws Exception;

    /**
     * Read up to length bytes from the current state into the buffer. This
     * method can be use to read a large amount of bytes from current state.
     * @param buffer - the data.
     * @param offset - the start offset of the data.
     * @param length - the maximum number of bytes read.
     * @return the total number of bytes read into buffer, or -1 if there is no
     * more data because the end of the allocate memory space for the state has
     * been reached.
     * @throws Exception - if an error occurs.
     */
    public int read(byte[] buffer, int offset, int length) throws Exception;

    /**
     * Read up a memory page from the current state. The state is organized in 
     * memory pages. Each page has a index, a maximum size and the amount of
     * bytes which is used in the page.
     * @param index - the memory page index.
     * @return - return a memory page of this state.
     * @throws Exception - if an error occurs.
     */
    public IPage readPage(long index)  throws Exception;

    /**
     * Write back a memory page to the current state. A new memory page is
     * allocated if the specified page doesn't exist.
     * @param page - the page
     * @throws Exception - if an error occurs.
     */
    public void writePage(IPage page) throws Exception;

    /**
     * Sets the state-pointer offset, measured from the begining of this state,
     * at which the next read or write occurs. The offset may be set beyond end
     * of the state. Setting the offset beyond the end of the state does not
     * change the amount of memory allocated for the state.
     * @param position - the offset position, measured in bytes from the
     * begining of the state, at which to set the state data pointer.
     * @throws Exception - if an error occurs.
     */
    public void seek(long position) throws Exception;

    /**
     * Returns the current state data offset in this state.
     * @return the offset from the begining of the state, in bytes, at which the
     * next read or write occurs.
     * @throws Exception - if an error occurs.
     */
    public long getPosition() throws Exception;

    /**
     * Returns the current allocated size of this state.
     * @return the current allocated size of this state, measured in bytes.
     * @throws Exception - if an error occurs.
     */
    public long getLength() throws Exception;

    /**
     * Sets the length of this state.
     * <P>
     * If the allocated size of the state as returned by the
     * <I>getLength()</I> method is greater than <B>newSize</B>
     * argument then the allocated bytes of this state will be truncated. In
     * this case, if the state offset as returned by the <B>getPosition</B>
     * method is greather than newSize then after this method returns the offset
     * will be equal to <B>newSize</B>.
     * </P>
     * <P>
     * If the present allocated size of the state as returned by the
     * <I>getLength()</I> method is smaller than <B>newSize</B>
     * argument then the size allocated will be extended. In this case, the
     * contents of the extended portion of the state are not defined.
     * </P>
     * @param newSize - the desired size to be allocated in the state, measured
     * in bytes.
     * @throws Exception - If an error occurs.
     */
    public void setLength(long newLength) throws Exception;

    /**
     * Returns the current number of pages of this state. It is measured using:
     * <P>
     * numberOfPages = Math.ceil(getCurrentAllocatedSize()/getPageSize());
     * </P>
     * @return the current number of pages of this state.
     * @throws Exception - if an I/O error occurs.
     */
    public long getCurrentNumberOfPages() throws Exception;

    /**
     * Returns a list of page indexes that were recently modified.
     * @return - the list of modified page indexes.
     * @throws Exception - if an I/O error occurs.
     */
    public PageIndexList getRecentlyModifiedPageIndexes() throws Exception;

    public void setOptions(Properties options) throws Exception;
    public Properties getOptions() throws Exception;

    public IMemory getMemory() throws Exception;


}
