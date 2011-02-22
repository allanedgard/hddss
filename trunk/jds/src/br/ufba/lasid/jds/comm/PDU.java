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
    public static String SOURCE = "__PDU_SOURCE";
    public static String DESTINATION = "__PDU_DESTINATION";
    public static String PAYLOAD = "PDU_PAYLOAD";
    public static String CONTROL = "PDU_CONTROL";

    public void setSource(Object src){
        put(SOURCE, src);
    }

    public Object getSource(){
        return get(SOURCE);
    }

    public void setDestination(Object dest){
        put(DESTINATION, dest);
    }


    public Object getDestination(){
        return get(DESTINATION);
    }

    public void setPayload(Object payload){
        put(PAYLOAD, payload);
    }

    public Object getPayload(){
        return get(PAYLOAD);
    }

}
