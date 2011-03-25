/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.adapters;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class EventHandler<T> implements InvocationHandler{

    private T object = null;
    private AfterEventtable aftertable;
    private BeforeEventtable beforetable;

    public static <T> T newInstance(T object, BeforeEventtable beforetable, AfterEventtable aftertable) {
        return (T)Proxy.newProxyInstance(
                    object.getClass().getClassLoader(),
                    object.getClass().getInterfaces(),
                    new EventHandler(object, beforetable, aftertable)
        );
    }

    private EventHandler(T object, BeforeEventtable beforetable, AfterEventtable aftertable){
        this.object = object;
        this.beforetable = beforetable;
        this.aftertable = aftertable;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result = null;

        try{
             ArrayList<IBeforeEventListener> befores = beforetable.get(method);
             if(befores != null && !befores.isEmpty()){
                 for(IBeforeEventListener before : befores){
                     before.before(method, object, args);
                 }
             }

            result = method.invoke(object, args);

        }catch(Exception e){

            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        }finally{

             ArrayList<IAfterEventListener> afters = aftertable.get(method);
             if(afters != null && !afters.isEmpty()){
                 for(IAfterEventListener after : afters){
                     after.after(method, object, result, args);
                 }
             }
        }

        return result;
    }

}
