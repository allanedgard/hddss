/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

import br.ufba.lasid.jds.comm.communicators.ICommunicator;
import trash.br.ufba.lasid.jds.Action;
import trash.br.ufba.lasid.jds.Executor;
import trash.br.ufba.lasid.jds.util.Context;
import trash.br.ufba.lasid.jds.util.ExecutorCollection;
import trash.br.ufba.lasid.jds.util.ExecutorMap;
import trash.br.ufba.lasid.jds.util.Wrapper;
import trash.br.ufba.lasid.jds.factories.ActionFactory;

/**
 *
 * @author aliriosa
 */
public class Protocol implements IProtocol{

    protected Context context = new Context();
    protected ICommunicator communicator = null;

    //public static String COMMUNICATOR   = "__ProtocolCommunicator";
    public static String TAG            = "protocol";

    public void setCommunicator(ICommunicator comm){

        communicator = comm;

    }

    public ICommunicator getCommunicator(){

        return communicator;

    }

    public Context getContext(){
        return context;
    }

    /*********************************
     * To execlude all bellow. (IMPORTANT)
     */
    ExecutorMap executors = new ExecutorMap();

    public ExecutorMap getExecutors(){
        return executors;
    }

    public void addExecutor(Class type, Executor executor){
        ExecutorCollection execs =  executors.get(type);
        if(execs == null){
            execs = new ExecutorCollection();
        }

        execs.add(executor);
        executors.put(type, execs);
    }

    public void delExecutors(Class type){
        executors.remove(type);
    }

    public ExecutorCollection getExecutors(Class type){
        return executors.get(type);
    }

    public void doAction(Wrapper w){
        //System.out.println("[DistributedProtocol] call DistributedProtocol.perform");
        /**
         * we can generalize this method by setting the action factory in the
         * config file. It avoids all subclass of the overrides this method
         * unnecesserily.
         */

        perform(ActionFactory.create(w));
    }

    public void perform(Action action){
        //System.out.println("[DistributedProtocol] call DistributedProtocol.perform");
        notify(action);
    }

    private void notify(Action action) {
        //System.out.println("[DistributedProtocol] call DistributedProtocol.notify");
        if(action == null){
            return;
        }
        
        Class type =  action.getClass();
        ExecutorCollection executorList = executors.get(type);

        if(executorList == null)
               return;

        for(Executor executor : executorList){
            executor.execute(action);
        }
    }
}

