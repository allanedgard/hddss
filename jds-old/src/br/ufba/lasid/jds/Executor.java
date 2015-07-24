/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

/**
 *
 * @author aliriosa
 */
public abstract class Executor<T> extends Thread implements IExecutor<T>{

    public Executor() {
        setName(this.getClass().getSimpleName());
    }


    @Override
    public void run() {
        execute();
    }

}
