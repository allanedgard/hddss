/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management;

import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public interface IProvider<T> {
    public T create(Properties options) throws Exception;
}
