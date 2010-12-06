/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

import br.ufba.lasid.jds.util.Wrapper;

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
    
    public Wrapper getWrapper(){
        return wrapper;
    }

}
