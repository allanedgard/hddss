/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.prototyping.hddss.examples.helloworldcs;

import br.ufba.lasid.jds.cs.ClientServerApplicationPayload;
import trash.br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import trash.br.ufba.lasid.jds.prototyping.hddss.cs.Agent_Client;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class HelloWorldClient extends Agent_Client{
        
    @Override
    public void execute() {
        
        HelloWorldPayload hello = new HelloWorldPayload();
        
        hello.put(HelloWorldPayload.OPERATION, "Hi Server!");

        /*
        ClientServerMessage m = new ClientServerMessage();
        m.put(ClientServerMessage.TYPEFIELD, ClientServerMessage.TYPE.SENDREQUEST);
        m.put(ClientServerMessage.PAYLOADFIELD, "Hi Server");
        m.put(ClientServerMessage.SOURCEFIELD, this);
        m.put(ClientServerMessage.DESTINATIONFIELD, getServerProcessAddressRef());
        getProtocol().doAction(m);
         * 
         */

        getProtocol().doAction(hello);
        
    }

    @Override
    public void receiveResult(IPayload content) {
        HelloWorldPayload hello  = (HelloWorldPayload) content;
        System.out.println("HelloWorldClient received: " + hello.get(HelloWorldPayload.OPERATION));
    }


    
}
