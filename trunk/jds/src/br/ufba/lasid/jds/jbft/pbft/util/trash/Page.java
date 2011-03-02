/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util.trash;

/**
 *
 * @author aliriosa
 */
public class Page extends Partition{
    
    private static final long serialVersionUID = -3794655302451562928L;

    Object value;
    
    public Page(){
        this(null);
    }

    public Page(Object value){
        super(0);
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isEmpty(){
        return this.value == null;
    }    

}
