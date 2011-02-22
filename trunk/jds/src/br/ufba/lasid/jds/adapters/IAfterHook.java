/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.adapters;

import java.lang.reflect.Method;

/**
 *
 * @author aliriosa
 */
public interface IAfterHook extends IHook{

    public void call(Method method, Object[] args, Object result);

}
