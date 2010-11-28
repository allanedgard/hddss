/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

import br.ufba.lasid.jds.Process;

/**
 *
 * @author aliriosa
 */
public interface Communicator {

    public void multicast(Message m, Process group);

    void unicast(Message m, Process process);

}
