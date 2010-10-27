/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss;

/**
 *
 * @author aliriosa
 */
public class Value {
    Object value;

    public <V> Value(V value){
        this.value = value;
    }

    public <V> V value(){
        return (V)value;
    }

}
