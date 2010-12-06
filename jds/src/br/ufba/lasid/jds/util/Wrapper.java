/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

/**
 *
 * @author aliriosa
 */
public interface Wrapper<K, V> {
    public Object getContent();
    public void setContent(Object content);
    public V get(K value);

}
