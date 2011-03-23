/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory;

/**
 *
 * @author aliriosa
 */
public interface IVolatileMemory extends IMemory{

    public void graft(byte[] b) throws Exception;
    public void graft(byte[] b, int off, int len) throws Exception;

}
