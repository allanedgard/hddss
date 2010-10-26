/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss;

/**
 *
 * @author aliriosa
 */
public interface Algorithm<T> {
    public T getOutput();
    public void setInput(T input);
    public long getLatency();
    public void execute();
}
