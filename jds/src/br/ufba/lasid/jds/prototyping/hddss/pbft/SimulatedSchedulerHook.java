/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.hdf.adapters.IBeforeHook;
import br.ufba.lasid.jds.util.ITask;
import java.lang.reflect.Method;
import br.ufba.lasid.jds.util.Agenda;
import br.ufba.lasid.jds.util.IClock;
import br.ufba.lasid.jds.util.IScheduler;
import br.ufba.lasid.jds.util.TaskList;

/**
 *
 * @author aliriosa
 */
public class SimulatedSchedulerHook implements IBeforeHook, IScheduler{

    protected volatile Agenda agenda = new Agenda();
    protected  final Object lock = this;
    protected IClock clock;
    
    public SimulatedSchedulerHook(IClock clock){
        this.clock = clock;
    }
    
    public boolean check(Method method) {
        return method.getName().equalsIgnoreCase("execute");
    }

    public void call(Method method, Object[] args) {
        if(check(method)){
            execute();
        }
    }

    public synchronized void execute(){

        Long time = clock.value();
        
        TaskList tasks = agenda.get(time);

        if(tasks != null){
            TaskList _tasks = new TaskList();
            _tasks.addAll(tasks);
            for(ITask task: _tasks){
                task.runMe();
            }

            tasks.clear();
            agenda.remove(time);
        }
        
    }

    public synchronized void schedule(ITask task, long time) {
        TaskList tasks = agenda.get(time);

        if(tasks == null){
            tasks = new TaskList();
        }

        if(!tasks.contains(task)){
            tasks.add(task);
            agenda.put(time, tasks);
        }
    }

    public synchronized boolean cancel(ITask task) {

        boolean cancelled = false;
        Agenda _agenda = new Agenda();
        
        _agenda.putAll(agenda);

        for(Long _time : _agenda.keySet()){

            TaskList tasks = _agenda.get(_time);

            TaskList _tasks = new TaskList();

            _tasks.addAll(tasks);

            for(ITask _task : _tasks){
                if(_task.equals(task)){
                    tasks.remove(_task);
                    cancelled = true;
                }
            }

            if(tasks.isEmpty()){
                agenda.remove(_time);            
            }
        }

        return cancelled;
    }

    public synchronized boolean cancelAll() {
        
        for(TaskList tasks : agenda.values()){
            tasks.clear();
        }

        agenda.clear();

        return true;
    }

    public void call(Object who, Method method, Object[] args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
