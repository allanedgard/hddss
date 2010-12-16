/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.util.Agenda;
import br.ufba.lasid.jds.util.Scheduler;
import br.ufba.lasid.jds.util.Task;
import br.ufba.lasid.jds.util.TaskList;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author aliriosa
 */
public class SimulatedScheduler extends Agent implements Scheduler{
    
    public static String TAG = "SimulatedScheduler";
    
    protected Agenda agenda = new Agenda();

    public void schedule(Task task, long time) {

        Long _time = new Long(time);
        
        TaskList tasks = agenda.get(_time);
        if(tasks == null){
            tasks = new TaskList();
        }

        tasks.add(task);

        agenda.put(_time, tasks);

        
    }

    @Override
    public void execute() {

        Long _time = new Long(infra.clock.value());

        TaskList tasks = agenda.get(_time);

        if(tasks != null){
            for(Task task : tasks){
                task.runMe();
            }
        }
        
    }

    public void cancel(Task task){
         Set<Entry<Long, TaskList>> set =  agenda.entrySet();

         for(Entry<Long, TaskList> e : set){

            if(e.getValue().equals(task)){

                agenda.remove(e.getKey());

                return;
            }
            
         }

    }

    public void cancelAll() {
        //throw new UnsupportedOperationException("Not supported yet.");
        //do nothing
    }
    
}
