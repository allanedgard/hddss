/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

import br.ufba.lasid.jds.comm.Communicator;
import br.ufba.lasid.jds.util.Context;
import br.ufba.lasid.jds.util.ExecutorCollection;
import br.ufba.lasid.jds.util.ExecutorMap;
import br.ufba.lasid.jds.util.Wrapper;
import br.ufba.lasid.jds.factories.ActionFactory;

/**
 *
 * @author aliriosa
 */
public class Protocol {

    Context context = new Context();

    ExecutorMap executors = new ExecutorMap();

    public static String COMMUNICATOR   = "__ProtocolCommunicator";
    public static String TAG            = "protocol";

    public void setCommunicator(Communicator comm){

        getContext().put(COMMUNICATOR, comm);

    }

    public Communicator getCommunicator(){

        return (Communicator)getContext().get(COMMUNICATOR);

    }

    public Context getContext(){
        return context;
    }

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

