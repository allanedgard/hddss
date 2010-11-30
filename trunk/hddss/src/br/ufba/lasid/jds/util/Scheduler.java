/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

/**
 *
 * @author aliriosa
 */
public interface Scheduler {

    public void schedule(Task task, long time);
    public void cancel(Task task);
      
}