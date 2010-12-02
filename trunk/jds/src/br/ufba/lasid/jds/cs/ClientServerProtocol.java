/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs;

import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.factories.ClientServerActionFactory;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class ClientServerProtocol extends Protocol{

    @Override
    public void doAction(Wrapper w) {
        //System.out.println("[Protocol] call ClientServerProtocol.perform");
        perform(ClientServerActionFactory.create(w));
    }

}
