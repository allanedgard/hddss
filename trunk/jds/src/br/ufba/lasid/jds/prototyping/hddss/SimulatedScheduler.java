/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.util.Agenda;
import br.ufba.lasid.jds.util.IScheduler;
import br.ufba.lasid.jds.util.ITask;
import java.util.Collection;
import br.ufba.lasid.jds.util.TaskList;

/**
 *
 * @author aliriosa
 */
public class SimulatedScheduler extends Agent implements IScheduler{
    
    public static String TAG = "SimulatedScheduler";
    
    protected  Agenda agenda = new Agenda();

    public synchronized void schedule(ITask task, long time) {

        Long _time = new Long(time);
        
        TaskList tasks = agenda.get(_time);
        if(tasks == null){
            tasks = new TaskList();
        }

        tasks.add(task);

        agenda.put(_time, tasks);

        
    }

    @Override
    public synchronized void execute() {

        Long _time = new Long(infra.clock.value());

        TaskList tasks = agenda.get(_time);

        if(tasks != null){
            TaskList tasks2 = new TaskList();

            tasks2.addAll(tasks);

            for(ITask task : tasks2){
                task.runMe();
                tasks.remove(task);
            }

            tasks2.clear();
            tasks.clear();
        }

        agenda.remove(_time);
        
    }

    public synchronized boolean cancel(ITask task){
        

        Collection<TaskList> taskLists = agenda.values();

        Agenda agenda2 = new Agenda();
        agenda2.putAll(agenda);
        
        for(Long _time : agenda2.keySet()){

            TaskList t2 = new TaskList();
            
            t2.addAll(agenda2.get(_time));
            
            for(ITask t : t2){
                if(t.equals(task)){
                    agenda.get(_time).remove(t);
                }
            }

            if(agenda.get(_time).isEmpty()){
                agenda.remove(_time);
            }

            return true;
        }
        
        return false;

    }

    public synchronized boolean cancelAll() {
        agenda.clear();
        return true;
    }
    
}
