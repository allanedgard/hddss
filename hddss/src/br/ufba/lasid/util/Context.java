/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.util;

import java.util.Hashtable;
/**
 *
 * @author aliriosa
 */
public class Context extends Hashtable<String, Object>{

    public void save(String name, Object value) {
        put(name, value);
    }

}
