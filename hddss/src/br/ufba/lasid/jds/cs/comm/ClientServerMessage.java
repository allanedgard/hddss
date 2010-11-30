/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs.comm;

import br.ufba.lasid.jds.comm.Message;
/**
 *
 * @author aliriosa
 */
public class ClientServerMessage extends Message{
    public enum TYPE{
        /*don't change this order ... you may get throuble!*/
        SENDREQUEST(0),
        RECEIVEREQUEST(1),
        EXECUTE(2),
        SENDREPLY(3),
        RECEIVEREPLY(4);

        private final int value;

        TYPE(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }
        
    }
/*
    public ClientServerMessage(TYPE type){
        setType(type);
    }

    public ClientServerMessage(TYPE type, Object content){
        this(type);
        setContent(content);
    }

    public ClientServerMessage(TYPE type, Object content, Process<T> source, Process<T> destination){
        this(type, content);
        setSource(source);
        setDestination(destination);
    }

    public void setType(TYPE type){
        setType(type.getValue());
    }
*/
}
