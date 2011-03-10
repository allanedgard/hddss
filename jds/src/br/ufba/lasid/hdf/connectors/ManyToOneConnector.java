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
import br.ufba.lasid.hdf.util.SupplierList;

/**
 *
 * @author aliriosa
 */
public class ManyToOneConnector extends Thread implements IConnector{

    SupplierList suppliers = new SupplierList();
    IConsumer consumer = null;
    boolean connected = false;
    ConnectorList connectors = new ConnectorList();

    public ManyToOneConnector() {
        setName(this.getClass().getSimpleName());
    }

    public void connect(ISupplier supplier, IConsumer consumer) {
        connectTo(consumer);
        connectTo(supplier);
    }

    public void connectTo(ISupplier supplier) {
        suppliers.add(supplier);
    }

    public void connectTo(IConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {

        for(ISupplier supplier : suppliers){
            OneToOneConnector connector = new OneToOneConnector();
            connector.connect(supplier, consumer);
            connectors.add(connector);
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
