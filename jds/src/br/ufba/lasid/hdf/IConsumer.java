/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf;

import org.apache.commons.collections.Buffer;

/**
 *
 * @author aliriosa
 */
public interface IConsumer {

    public Buffer getInbox();

    public boolean canConsume(Object object);

}
