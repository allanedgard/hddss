/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.connectors;

import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.hdf.ISupplier;

/**
 *
 * @author aliriosa
 */
public class OneToOneConnector extends Thread implements IConnector{
    ISupplier supplier = null;
    IConsumer consumer = null;
    boolean connected = false;
    
    public void connect(ISupplier supplier, IConsumer consumer) {
        connectTo(supplier);
        connectTo(consumer);
    }

    public void connectTo(ISupplier supplier) {
        this.supplier = supplier;
    }

    public void connectTo(IConsumer consumer) {
        this.consumer = consumer;
    }

    public IConsumer getConsumer() {
        return consumer;
    }

    public ISupplier getSupplier() {
        return supplier;
    }

    @Override
    public void run() {
        connected = true;
        while(connected){

            try{
                Object obj = supplier.getOutbox().remove();
                if(consumer.canConsume(obj)){
                    consumer.getInbox().add(obj);
                }
            }catch(Exception e){
                e.printStackTrace();
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
