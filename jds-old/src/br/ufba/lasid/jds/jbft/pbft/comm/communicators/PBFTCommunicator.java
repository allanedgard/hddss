/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm.communicators;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.communicators.ICommunicator;
import br.ufba.lasid.hdf.ISupplier;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

/**
 *
 * @author aliriosa
 */
public abstract class  PBFTCommunicator extends Thread implements ISupplier, ICommunicator{

    protected  Buffer outbox = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());

    public PBFTCommunicator() {
        setName(this.getClass().getSimpleName());
    }
    
    public Buffer getOutbox() {
        return this.outbox;
    }

    public void receive(IMessage m) {
        synchronized(this){
//        Debugger.debug("" + m);
            this.outbox.add(m);
        }
    }

}
