/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.events;

/**
 *
 * @author aliriosa
 */
public interface IEvent<T> {

    public enum ActivationType{
        before, after;
    }

    public Object getSource();
    public T getInfo();
    public ActivationType getActivationType();
    public long getTimestamp();

}
