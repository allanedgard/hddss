/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

/**
 *
 * @author aliriosa
 */
public class SingleProcess<T> implements Process<T>{
    T id = null;
    public T getID() {
        return id;
    }

    public void setID(T id) {
        this.id = id;
    }

}
