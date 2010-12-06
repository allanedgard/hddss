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
        CREATEREQUEST(-1),
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
}
