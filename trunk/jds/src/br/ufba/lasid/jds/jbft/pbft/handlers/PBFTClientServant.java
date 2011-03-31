/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.client.PBFTClient;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

/**
 *
 * @author aliriosa
 */
public class PBFTClientServant extends PBFTClientMessageHandler implements IConsumer{
    /**
     * Instantiates a new PBFTServerServant.
     * @param protocol - the pbft protocol.
     */
    public PBFTClientServant(PBFTClient protocol){
        super(protocol);
        setName(this.getClass().getSimpleName() + "[" + protocol.getLocalProcessID()+"]");
    }

    /**
     * Input buffer which is used to keep the received messages before they be able
     * to be checked and processed.
     */
    protected  Buffer inbox = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());

    public Buffer getInbox() {
        return inbox;
    }

    /**
     *
     * @param object
     * @return - true if the object is a instance of IMessage (e.g. SignedMessage,
     * PDU, PBFTRequest, PBFTPrePrepare, PBFTPrepare, etc.), otherwise returns false.
     */
    public boolean canConsume(Object object) {

        return (object instanceof IMessage);

    }

    /**
     * Overrides the run method and
     */
    @Override
    public void run() {

        while(true){
            IMessage message = (IMessage) inbox.remove();
            input(message);
            handle();
        }

    }

    /**
     * Used to read the input buffer, instantiates, delivers the inputed message
     * and starts the dispatcher handler.
     */
    public void handle() {
        //try {
            IMessage message = extract(this.input);

            if(message != null && message instanceof PBFTReply){
                PBFTReplyHandler handler = new PBFTReplyHandler(getProtocol());
                handler.input(message);
                handler.handle();
            }
            //handler.start();
            //handler.join();

//        }// catch (InterruptedException ex) {
//            Logger.getLogger(PBFTServerServant.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
//        }

    }

    public IMessage extract(IMessage m){

        if(m instanceof PDU){
            PDU pdu = (PDU) m;
            return extract((IMessage) pdu.getPayload());
        }

        if(m instanceof SignedMessage){
            SignedMessage signedMessage = (SignedMessage) m;
            try {
                if(getProtocol().getAuthenticator().check(signedMessage)){
                    return (IMessage) signedMessage.getSignedObject().getObject();
                }
                return null;
            } catch (Exception except) {
                Logger.getLogger(PBFTClientServant.class.getName()).log(Level.SEVERE, null, except);
                except.printStackTrace();
            }
        }

        return null;
    }

}

