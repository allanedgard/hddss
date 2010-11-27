/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft;

/**
 *
 * @author aliriosa
 */
public class Message implements Wrapper{
    private int type;
    
    public int getType(){
        return type;
    }

    public void setType(int type){
        this.type = type;
    }
}
