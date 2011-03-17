/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.jds.comm.IEvent;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

/**
 *
 * @author aliriosa
 */
public class PBFTServerEventHandler extends PBFTServerMessageHandler implements IConsumer{
    /**
     * Instantiates a new PBFTServerServant.
     * @param protocol - the pbft protocol.
     */
    public PBFTServerEventHandler(PBFTServer protocol){
        super(protocol);
        setName(this.getClass().getSimpleName() + "[" + protocol.getLocalServerID()+"]");
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

        return (object instanceof IEvent);

    }

    /**
     * Overrides the run method and
     */
    @Override
    public void run() {

        while(true){
            handle();
        }

    }

    /**
     * Used to read the input buffer, instantiates, delivers the inputed message
     * and starts the dispatcher handler.
     */
    public void handle() {
            IEvent message = (IEvent) inbox.remove();
//            PBFTServerMessageDispatcher handler = new PBFTServerMessageDispatcher(getProtocol());
//            handler.input(message);
//            handler.handle();
            //handler.start();

    }

}
