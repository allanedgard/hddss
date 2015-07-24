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
public interface IForwarder<T> {

    public void connectTo(IConsumer consumer);
    public void foward(T data);

}
