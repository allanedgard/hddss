/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.adapters;

import java.lang.reflect.Method;

/**
 *
 * @author aliriosa
 */
public interface IHook {

    public boolean check(Method method);
    public void call(Object who, Method method, Object[] args);

}
