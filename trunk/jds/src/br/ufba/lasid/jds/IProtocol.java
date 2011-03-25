/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

import br.ufba.lasid.jds.comm.communicators.ICommunicator;

/**
 *
 * @author aliriosa
 */
public interface IProtocol {

    public void setCommunicator(ICommunicator comm);

    public ICommunicator getCommunicator();


}
