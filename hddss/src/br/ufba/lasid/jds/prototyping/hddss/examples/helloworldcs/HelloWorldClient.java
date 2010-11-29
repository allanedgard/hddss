/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.helloworldcs;

import br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import br.ufba.lasid.jds.prototyping.hddss.cs.Agent_Client;

/**
 *
 * @author aliriosa
 */
public class HelloWorldClient extends Agent_Client{
        
    @Override
    public void execute() {
        ClientServerMessage m =
                new ClientServerMessage(
                    ClientServerMessage.TYPE.SENDREQUEST,
                    "Hi Server!", 
                    this,
                    getServerProcessAddressRef()
                );

        getProtocol().doAction(m);
    }

    @Override
    public void receiveReply(Object content) {
        System.out.println(content);
    }


    
}
