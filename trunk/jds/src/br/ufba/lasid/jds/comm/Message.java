/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

import br.ufba.lasid.jds.util.Wrapper;
import java.util.Hashtable;
/**
 *
 * @author aliriosa
 */
public class Message extends Hashtable<String, Object> implements Wrapper{
    public static String TYPEFIELD = "__TYPEFIELD";
    public static String PAYLOADFIELD = "__PAYLOADFIELD";
    public static String SOURCEFIELD = "__SOURCEFIELD";
    public static String DESTINATIONFIELD = "__DESTINATIONFIELD";

    public Object getContent() {
        return get(PAYLOADFIELD);
    }

    public void setContent(Object content) {
        put(PAYLOADFIELD, content);
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
/*    private int type;
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
*/
}
