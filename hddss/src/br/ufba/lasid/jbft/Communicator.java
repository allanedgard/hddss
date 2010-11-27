/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft;

/**
 *
 * @author aliriosa
 */
public interface Communicator {

    public void multicast(Message m, Process group);

    void unicast(Message m, Process client);

}
