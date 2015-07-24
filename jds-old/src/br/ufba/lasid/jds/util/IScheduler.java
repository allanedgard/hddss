/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

/**
 *
 * @author aliriosa
 */
public interface IScheduler {

//    public void schedule(ITask task, long time);
//    public boolean cancel(ITask task);
//    public boolean cancelAll();

    public ISchedule newSchedule();

//    protected void schedule(ISchedule schedule);

    public void execute();

    public ISchedule schedule(ITask task, long time);
    public void cancel(ISchedule schedule);
      
}