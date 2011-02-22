/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.connectors;

import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.hdf.ISupplier;
import br.ufba.lasid.hdf.ISupplier;
import br.ufba.lasid.hdf.util.ConnectorList;
import br.ufba.lasid.hdf.util.ConsumerList;
import br.ufba.lasid.hdf.util.SupplierList;

/**
 *
 * @author aliriosa
 */
public class ManyToManyConnector extends Thread implements IConnector{

    SupplierList suppliers = new SupplierList();
    ConsumerList consumers = new ConsumerList();
    ConnectorList connectors = new ConnectorList();
    boolean connected = false;
    public void connect(ISupplier supplier, IConsumer consumer) {
        this.connectTo(supplier);
        this.connectTo(consumer);
    }

    public void connectTo(ISupplier supplier) {
        suppliers.add(supplier);
    }

    public void connectTo(IConsumer consumer) {
        consumers.add(consumer);
    }

    @Override
    public void run() {

        for(ISupplier supplier : suppliers){

            OneToManyConnector connector = new OneToManyConnector();
            
            for(IConsumer consumer : consumers){
                connector.connect(supplier, consumer);
                connectors.add(connector);
            }

            connector.start();
        }

        connected = true;

    }

    public void disconnect() {
        connectors.disconnect();
        connected = false;
    }

    public boolean connected() {
        return this.connected;
    }



}
