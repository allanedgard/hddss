/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.connectors;

import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.hdf.IConsumer;

/**
 *
 * @author aliriosa
 */
public class ForwardConnector implements IForwarder{

    IConsumer consumer;


    public void connectTo(IConsumer consumer) {
        this.consumer = consumer;
    }

    public void foward(Object data) {
        consumer.getInbox().add(data);
    }


}
