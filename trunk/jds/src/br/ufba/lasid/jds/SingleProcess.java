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

    @Override
    public boolean equals(Object obj) {
        Process<T> process = (Process<T>) obj;
        return id.equals(process.getID());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }



}
