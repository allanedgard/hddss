/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.Agenda;
import br.ufba.lasid.jds.Scheduler;
import br.ufba.lasid.jds.Task;
import br.ufba.lasid.jds.TaskList;

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

        for(Task task : tasks){
            task.runMe();
        }
        
    }



}
