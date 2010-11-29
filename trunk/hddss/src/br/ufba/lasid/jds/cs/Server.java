/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs;
/**
 *
 * @author aliriosa
 */
public interface Server<T> extends br.ufba.lasid.jds.Process<T>{
    public Object doService(Object arg);
}
