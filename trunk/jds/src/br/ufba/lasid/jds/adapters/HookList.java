/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.adapters;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class HookList extends ArrayList<IHook> implements IHook{

    public boolean check(Method method) {
        
        for(IHook hook : this){
            if(hook.check(method)){
                return true;
            }
        }
        return false;
    }

    public void call(Method m, Object[] args) {
        for(IHook hook: this){
            if(hook.check(m)){
                hook.call(m, args);
            }
        }
    }

}
