/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.util;

import br.ufba.lasid.util.actions.Action;
import br.ufba.lasid.util.actions.ActionFactory;

/**
 *
 * @author aliriosa
 */
public class Protocol {

    Process process = null;
    
    Context context = null;

    ExecutorMap executors = new ExecutorMap();

    Communicator comm;
    
    public Communicator getCommunicator() {
        return this.comm;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public void setCommunicator(Communicator comm){
        this.comm = comm;
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
        System.out.println("[Protocol] call Protocol.perform");
        perform(ActionFactory.create(w));
    }

    public void perform(Action action){
        System.out.println("[Protocol] call Protocol.perform");
        notify(action);       
    }

    private void notify(Action action) {
        System.out.println("[Protocol] call Protocol.notify");
        Class type =  action.getClass();
        ExecutorCollection executorList = executors.get(type);

        if(executorList == null)
               return;
        
        for(Executor executor : executorList){
            executor.execute(action);
        }
    }    
}

