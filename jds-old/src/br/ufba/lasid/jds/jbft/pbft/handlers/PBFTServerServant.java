package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.server.PBFTServer;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

/**
 * A thread-based implementation that reads a input buffer with the received
 * messages and creates a message dispatcher to select the required handler for
 * performing the message processing.
 * @author aliriosa
 */
public class PBFTServerServant extends PBFTServerMessageHandler implements IConsumer{
    /**
     * Instantiates a new PBFTServerServant.
     * @param protocol - the pbft protocol.
     */
    public PBFTServerServant(IPBFTServer protocol){
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
            IMessage message = this.input;
            PBFTServerMessageDispatcher handler = new PBFTServerMessageDispatcher(getProtocol());
            handler.input(message);
            handler.handle();
            //handler.start();
        
    }

}
