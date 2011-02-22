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

    @Override
    public void run() {
        execute();
    }

}
