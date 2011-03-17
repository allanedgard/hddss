/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.cache;

import br.ufba.lasid.jds.management.memory.IMemory;

/**
 *
 * @author aliriosa
 */
public interface ICache extends IMemory{

    /**
     * Sets the maximum size in pages of this cache.
     * <P>
     * If maximum size of the cache as returned by the <I>getMaxSize</I> method 
     * is greater than <B>newSize</B> argument then exceeded pages of this cache
     * memory will be discarded.
     * </P>
     * <P>
     * If the present maximum size of the memory as returned by the 
     * <I>getMaxSize</I> method is smaller than <B>newSize</B> argument then
     * maximum size will be extended.
     * </P>
     * @param newSize - the new size measured in pages of this cache.
     * @throws Exception - if an I/O error occurs.
     */
    public void setMaxSize(int newSize) throws Exception;

    /**
     * Returns the maximum size, measured in pages, of this cache.
     * @return - the maximum size, measured in pages, of this cache.
     * @throws Exception - if an I/O error occurs.
     */
    public int getMaxSize() throws Exception;

    /**
     * Sets the cache policy which will be used by this cache.
     * @param policy - the cache policy which will be used by this cache.
     * @throws Exception - if an I/O error occurs.
     */
    public void setPolicy(ICachePolicy policy) throws Exception;
    /**
     * Returns the cache policy defined of this cache.
     * @return - the cache policy defined of this cache
     * @throws Exception - if an I/O error occurs.
     */
    public ICachePolicy getPolicy() throws Exception;

}
