/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs;

import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.factories.ClientServerActionFactory;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class ClientServerProtocol extends DistributedProtocol{

    @Override
    public void doAction(Wrapper w) {
        perform(ClientServerActionFactory.create(w));
    }

}