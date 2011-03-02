/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.adapters;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 *
 * @author aliriosa
 */
public class Adapter<T> implements InvocationHandler{

    private T object = null;
    private HookList hooks = new HookList();
    
    public static <T> T newInstance(T object, IHook ... hooks) {
        return (T)Proxy.newProxyInstance(
                    object.getClass().getClassLoader(),
                    object.getClass().getInterfaces(),
                    new Adapter(object, hooks)
        );
    }

    private Adapter(T object, IHook ... hooks){
        this.object = object;
        this.hooks.addAll(Arrays.asList(hooks));
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        Object result = null;
        
        try{
            
            for(IHook hook : this.hooks){
                if(hook instanceof IBeforeHook){
                    if(hook.check(method)){
                        hook.call(object, method, args);
                    }
                }
            }
            System.out.println("*********** before ***********");
            result = method.invoke(object, args);

        }catch(Exception e){

            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        }finally{
            
            for(IHook hook : this.hooks){
                if(hook instanceof IAfterHook){
                    if(hook.check(method)){
                        ((IAfterHook)hook).call(object, method, args, result);
                    }
                }
            }
            System.out.println("*********** after ***********");
        }
        
        return result;
    }

}
