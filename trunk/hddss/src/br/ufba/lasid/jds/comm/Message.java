/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

import br.ufba.lasid.jds.util.Wrapper;
import br.ufba.lasid.jds.Process;
import java.util.Hashtable;
/**
 *
 * @author aliriosa
 */
public class Message<T> extends Hashtable<String, Object> implements Wrapper{
    private int type;
    private Object content;
    private Process<T> source;
    private Process<T> destination;

    public Process<T> getDestination() {
        return destination;
    }

    public void setDestination(Process<T> destination) {
        this.destination = destination;
    }

    public Process<T> getSource() {
        return source;
    }

    public void setSource(Process<T> source) {
        this.source = source;
    }
    
    

    public int getType(){
        return type;
    }

    public void setType(int type){
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

}
