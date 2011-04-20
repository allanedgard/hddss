/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.util.Agenda;
import br.ufba.lasid.jds.util.IClock;
import br.ufba.lasid.jds.util.ISchedule;
import br.ufba.lasid.jds.util.ITask;
import br.ufba.lasid.jds.util.IScheduler;
import br.ufba.lasid.jds.util.ScheduleList;

/**
 *
 * @author aliriosa
 */
public class SimulatedScheduler implements IScheduler{
    Agenda agenda = new Agenda();
    private int count = -1;
    private IClock clock;
    private long lastExecution = -1;

    public SimulatedScheduler(IClock clock) {
        this.clock = clock;
    }

    
    public ISchedule newSchedule() {
        Schedule schedule = new Schedule();
        schedule.scheduleID = count;
        count ++;

        return schedule;
    }

    public void execute() {

        long finalExecution = clock.value();
        long startExecution = lastExecution + 1;
        for(long now = startExecution; now <= finalExecution; now++){
           ScheduleList schedules = agenda.get(now);
           if(schedules != null){
               for(int i = schedules.size()-1; i >= 0; i--){
                   ISchedule schedule = schedules.get(i);
                   if(schedule.getTimestamp() == now){
                       schedule.execute();
                       //schedule.cancel();
                   }
                   schedules.remove(i);
               }
               agenda.remove(now);
           }
        }
        lastExecution = finalExecution;
    }

    public ISchedule schedule(ITask task, long timestamp) {
        ISchedule schedule = newSchedule();
        schedule.setTask(task);
        schedule.schedule(timestamp);
        return schedule;
    }

    public void cancel(ISchedule schedule) {
        schedule.cancel();
    }

    class Schedule implements ISchedule{
        protected ITask task;
        protected int  scheduleID;
        protected long timestamp;
        
        public void setTask(ITask task) {
                this.task = task;
        }

        public ITask getTask() {
            return this.task;
        }

        public void execute() {
            if(task != null){
                task.runMe();
            }
        }

        public int getScheduleID() {
            return this.scheduleID;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public void schedule(long timestamp){
            this.timestamp = timestamp;
            ScheduleList schedules = agenda.get(timestamp);
            if(schedules == null){
                schedules = new ScheduleList();
                agenda.put(timestamp, schedules);
            }
            schedules.add(this);
        }

        public void cancel() {
            this.timestamp = -1;
        }

        public void reschedule(long timestamp) {
            schedule(timestamp);
        }

        public boolean working() {
            return workingAt(clock.value());
        }

        public boolean workingAt(long time) {
            return this.timestamp >= time;
        }
        
    }

}
