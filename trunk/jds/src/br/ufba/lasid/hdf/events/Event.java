/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.events;

import br.ufba.lasid.hdf.events.IEvent.ActivationType;

/**
 *
 * @author aliriosa
 */
public class Event<T> implements IEvent<T>{

    long timestamp = 0;
    Object source;
    T info;
    IEvent.ActivationType activationType;
    
    public Event(Object source, T info, long timestamp, IEvent.ActivationType activationType){
        this.source = source;
        this.info = info;
        this.timestamp = timestamp;
        this.activationType = activationType;
    }
    
    public Object getSource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public T getInfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getTimestamp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ActivationType getActivationType() {
        return activationType;
    }

}
