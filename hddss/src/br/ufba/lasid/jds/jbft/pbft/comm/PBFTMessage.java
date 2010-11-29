/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTMessage<T> extends ClientServerMessage<T>{
    
    public enum TYPE{

        SENDREQUEST(ClientServerMessage.TYPE.SENDREQUEST.getValue()),
        RECEIVEREQUEST(ClientServerMessage.TYPE.RECEIVEREQUEST.getValue()),
        EXECUTE(ClientServerMessage.TYPE.EXECUTE.getValue()),
        SENDREPLY(ClientServerMessage.TYPE.SENDREPLY.getValue()),
        RECEIVEREPLY(ClientServerMessage.TYPE.RECEIVEREQUEST.getValue()),
        PREPREPARE(ClientServerMessage.TYPE.values().length),
        PREPARE(ClientServerMessage.TYPE.values().length + 1),
        COMMIT(ClientServerMessage.TYPE.values().length + 2);
      
        private final int value;

        TYPE(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }

        public ClientServerMessage.TYPE castToSuperType(){
            return ClientServerMessage.TYPE.valueOf(this.name());
        }
    }

    protected static long SEQ = 0;
    protected long mySEQ = 0;

    public static long newSequenceNumber(){
        return ++SEQ;
    }
    public long getSequenceNumber(){
        return mySEQ;
    }

    public void setSequenceNumber(long newSEQ){
        mySEQ = newSEQ;
    }

    
    public PBFTMessage(TYPE type, Object content, Process<T> source, Process<T> destination) {
      super(type.castToSuperType(), content, source, destination);
    }

    public PBFTMessage(TYPE type, Object content) {
        super(type.castToSuperType(), content);
    }

    public PBFTMessage(TYPE type) {
        super(type.castToSuperType());
    }

    public void setType(TYPE type){
        setType(type.getValue());
    }

}
