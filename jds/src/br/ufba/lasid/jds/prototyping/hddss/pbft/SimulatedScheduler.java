/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.util.ITask;
import br.ufba.lasid.jds.util.Agenda;
import br.ufba.lasid.jds.util.IClock;
import br.ufba.lasid.jds.util.IScheduler;
import br.ufba.lasid.jds.util.TaskList;

/**
 *
 * @author aliriosa
 */
public class SimulatedScheduler implements IScheduler{

    protected  Agenda agenda = new Agenda();
    protected  final Object lock = this;
    protected IClock clock;
    protected Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }    
    
    public SimulatedScheduler(IClock clock){
        this.clock = clock;
    }
    
    public void execute(){
//        synchronized(this){

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
  //      }
    }

    public void schedule(ITask task, long time) {
    //    synchronized(this){
            TaskList tasks = agenda.get(time);

            if(tasks == null){
                tasks = new TaskList();
            }

            if(!tasks.contains(task)){
                tasks.add(task);
                agenda.put(time, tasks);
            }
      //  }
    }

    public boolean cancel(ITask task) {
        //synchronized(this){

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
        //}
    }

    public boolean cancelAll() {
//        synchronized(agent.lock){
            for(TaskList tasks : agenda.values()){
                tasks.clear();
            }

            agenda.clear();
  //      }
        return true;
   }

}
