/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class Queue<T> extends ArrayList<T>{

    public void append(T item){
        add(item);
    }

    public T remove(){
        T item = null;
        if(!isEmpty()){
            item = get(0);
            remove(0);
        }

        return item;
    }


}
