/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.util.actions;

import br.ufba.lasid.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class Action {

    Wrapper wrapper;

    public Action(){
        
    }

    public Action(Wrapper w){
        wrapper = w;
    }
    
    public Wrapper getMessage(){
        return wrapper;
    }

}
