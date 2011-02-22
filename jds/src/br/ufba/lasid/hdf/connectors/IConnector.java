/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.connectors;

import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.hdf.ISupplier;
import br.ufba.lasid.hdf.ISupplier;

/**
 *
 * @author aliriosa
 */
public interface IConnector {

    public void connect(ISupplier supplier, IConsumer consumer);
    
    public void connectTo(ISupplier supplier);

    public void connectTo(IConsumer consumer);

    public void start();
    public void stop();
    public void disconnect();
    public boolean connected();

}
