/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.comm;

import trash.br.ufba.lasid.jds.IData;
import br.ufba.lasid.jds.util.IPayload;
import trash.br.ufba.lasid.jds.util.Wrapper;
import java.io.Serializable;
/**
 *
 * @author aliriosa
 */
public class Message /*extends Hashtable<String, Object>*/ implements Wrapper, IData, Serializable{
//    public static String TYPEFIELD = "__TYPEFIELD";
//    public static String PAYLOADFIELD = "__PAYLOADFIELD";
//    public static String SOURCEFIELD = "__SOURCEFIELD";
//    public static String DESTINATIONFIELD = "__DESTINATIONFIELD";
   IPayload content;
   Object source;
   Object destination;
    public IPayload getPayload() {
        return content;
    }

    public void setPayload(IPayload payload) {
        content = payload;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getContent() {
        return getPayload();
    }

    public void setContent(Object content) {
        setPayload((IPayload)content);
        //throw new UnsupportedOperationException("Not supported yet.");
    }

//    public Object getType() {
//        return get(TYPEFIELD);
//    }

    public void setSourceID(Object source){
        this.source = source;
    }

    public Object getSourceID(){
        return this.source;
    }

    public void setDestinationID(Object destination){
        this.destination = destination;
        //put(DESTINATIONFIELD, source);
    }

    public Object getDestinationID(){
        return this.destination;
    }

    public Object get(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
