/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory;

/**
 *
 * @author aliriosa
 */
public interface IObjectMemory {

    public void write(Object object) throws Exception;
    
    public Object read() throws Exception;

}
