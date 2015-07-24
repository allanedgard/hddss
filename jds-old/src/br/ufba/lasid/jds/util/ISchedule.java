/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

/**
 *
 * @author aliriosa
 */
public interface ISchedule {
    public void setTask(ITask task);
    public ITask getTask();
    public void execute();

    public int getScheduleID();

    public long getTimestamp();

    public void cancel();
    
    public void schedule(long timestamp);
    public void reschedule(long timestamp);

    public boolean working();
    public boolean workingAt(long time);
}
