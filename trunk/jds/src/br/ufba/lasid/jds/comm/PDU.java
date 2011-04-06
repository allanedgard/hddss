/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

import java.util.Hashtable;

/**
 *Protocol Data Unit
 * @author aliriosa
 */
public class PDU extends Hashtable<String, Object> implements IMessage{
    public static transient String SOURCE = "__PDU_SOURCE";
    public static transient String DESTINATION = "__PDU_DESTINATION";
    public static transient String PAYLOAD = "PDU_PAYLOAD";
    public static transient String CONTROL = "PDU_CONTROL";
    protected transient Object source;
    protected transient Object destination;
    protected Object payload;
    public void setSource(Object src){
       source = src;
        //put(SOURCE, src);
    }

    public Object getSource(){
       return source;
        //return get(SOURCE);
    }

    public void setDestination(Object dest){
       destination = dest;
        //put(DESTINATION, dest);
    }


    public Object getDestination(){
       return destination;
        //return get(DESTINATION);
    }

    public void setPayload(Object payload){
        //put(PAYLOAD, payload);
       this.payload = payload;
    }

    public Object getPayload(){
       return payload;
        //return get(PAYLOAD);
    }

}
