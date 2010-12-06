/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.group;

import br.ufba.lasid.jds.Process;
import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class SingleGroup<T> implements Group<T>{
    T id;
    int size = 0;
    ArrayList<Process<T>> members = new ArrayList<Process<T>>();
    public void setGroupID(T id) {
        this.id = id;
    }

    public T getGroupID() {
        return this.id;
    }

    public T getID() {
        return getGroupID();
    }

    public void setID(T id) {
        setGroupID(id);
    }

    public int getGroupSize() {
        return size;
    }

    public void setGroupSize(int size) {
        this.size = size;
    }

}
