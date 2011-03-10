/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.hdf.ISupplier;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTExecutorBroker<Input, Product> extends PBFTCollectorServant<Input> implements ISupplier{

    protected  Buffer outbox = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());
    
    public synchronized Buffer getOutbox() {
        return outbox;
    }

    public synchronized void store(Product product){
        outbox.add(product);
    }

}
