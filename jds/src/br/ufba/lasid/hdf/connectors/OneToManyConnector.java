/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.connectors;

import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.hdf.ISupplier;
import br.ufba.lasid.hdf.util.ConsumerList;

/**
 *
 * @author aliriosa
 */
public class OneToManyConnector extends Thread implements IConnector{
    ISupplier supplier = null;
    ConsumerList consumers = new ConsumerList();
    boolean connected = false;
    public void connect(ISupplier supplier, IConsumer consumer) {
        connectTo(supplier);
        connectTo(consumer);
    }

    public void connectTo(ISupplier supplier) {
        this.supplier = supplier;
    }

    public void connectTo(IConsumer consumer) {

        if(!consumers.contains(consumer)){
            consumers.add(consumer);
        }
        
    }

    @Override
    public void run() {
        connected = true;
        while(connected){
            
            Object obj = supplier.getOutbox().remove();

            for(IConsumer consumer : consumers){
                if(consumer.canConsume(obj)){
                    consumer.getInbox().add(obj);
                }
            }
        }
        
    }

    public void disconnect() {
        connected = false;
    }

    public boolean connected() {
        return this.connected;
    }

}
