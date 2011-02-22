/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

import br.ufba.lasid.jds.util.ITask;

/**
 *
 * @author aliriosa
 */
public interface IScheduler {

    public void schedule(ITask task, long time);
    public boolean cancel(ITask task);
    public boolean cancelAll();
      
}