/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors.PBFTRequestCollectorServant;
import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTCollectorServant<T> extends PBFTExecutor<SignedMessage> implements IConsumer{
    /**
     * Input buffer which is used to keep the received messages before they be able
     * to be checked and processed.
     */
    protected  Buffer inbox = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());
    

    /**
     * Get the input buffer.
     * @return the input buffer.
     */
    public synchronized Buffer getInbox() { return inbox;  }
    volatile Object itemBox;
    public void execute(){

        while(true){
            try{

//                synchronized(this){
                    itemBox = getInbox().remove();
                    SignedMessage m = extract(itemBox);

                    if(m != null && canConsume(m.getSignedObject().getObject())){

                        execute(m);
                    }
  //              }

            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
    }

    protected SignedMessage extract(Object obj){

        if(obj instanceof SignedMessage)
            return (SignedMessage)obj;

        if(obj instanceof PDU){
            return (SignedMessage)((PDU)obj).getPayload();
        }

        return null;
    }

    public void execute(SignedMessage m) {

        PBFT pbft = (PBFT)getProtocol();

        if(pbft.getAuthenticator().check(m)){
            try {
                accept((T) (m.getSignedObject().getObject()));

            } catch (Exception ex) {
                Logger.getLogger(PBFTRequestCollectorServant.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }

    }

    protected abstract boolean accept(T m);


}
