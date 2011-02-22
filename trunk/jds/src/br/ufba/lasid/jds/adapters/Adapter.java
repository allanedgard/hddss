/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.adapters;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 *
 * @author aliriosa
 */
public class Adapter implements InvocationHandler{

    private Object object = null;
    private HookList hooks = new HookList();
    
    public static Object newInstance(Object object, IHook ... hooks) {
        return Proxy.newProxyInstance(
                    object.getClass().getClassLoader(),
                    object.getClass().getInterfaces(),
                    new Adapter(object, hooks)
        );
    }

    private Adapter(Object object, IHook ... hooks){
        this.object = object;
        this.hooks.addAll(Arrays.asList(hooks));
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        Object result = null;
        
        try{
            
            for(IHook hook : this.hooks){
                if(hook instanceof IBeforeHook){
                    if(hook.check(method)){
                        hook.call(method, args);
                    }
                }
            }
            
            result = method.invoke(object, args);

        }catch(Exception e){

            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        }finally{
            
            for(IHook hook : this.hooks){
                if(hook instanceof IAfterHook){
                    if(hook.check(method)){
                        ((IAfterHook)hook).call(method, args, result);
                    }
                }
            }            
        }
        
        return result;
    }

}
