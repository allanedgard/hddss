/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft;

import br.ufba.lasid.jbft.actions.Action;
import br.ufba.lasid.jbft.actions.ActionFactory;

/**
 *
 * @author aliriosa
 */
public class Protocol {
    
    Context context = null;

    ExecutorMap executors = new ExecutorMap();

    Communicator comm;
    
    public Communicator getCommunicator() {
        return this.comm;
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

        executors.put(type, execs);
    }

    public void delExecutors(Class type){
        executors.remove(type);
    }

    public ExecutorCollection getExecutors(Class type){
        return executors.get(type);
    }

    public void doAction(Wrapper w){
        perform(ActionFactory.create(w));
    }

    public void perform(Action action){
        notify(action);       
    }

    private void notify(Action action) {
        Class type =  action.getClass();
        for(Executor executor : executors.get(type)){
            executor.execute(action);
        }
    }    
}

