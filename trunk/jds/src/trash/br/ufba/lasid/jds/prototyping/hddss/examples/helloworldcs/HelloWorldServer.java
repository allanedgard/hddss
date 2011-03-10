/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.prototyping.hddss.examples.helloworldcs;

import trash.br.ufba.lasid.jds.prototyping.hddss.cs.Agent_Server;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class HelloWorldServer extends Agent_Server{

    @Override
    public IPayload executeCommand(IPayload arg) {
        HelloWorldPayload hello = (HelloWorldPayload) arg;

        System.out.println("HelloWorldServer received: " + hello.get(HelloWorldPayload.OPERATION));

        hello.put(HelloWorldPayload.OPERATION, "Hello Client!");

        return hello;
        
    }

}
