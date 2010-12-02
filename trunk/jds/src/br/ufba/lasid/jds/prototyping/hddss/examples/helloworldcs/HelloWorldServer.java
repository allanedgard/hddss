/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.helloworldcs;

import br.ufba.lasid.jds.prototyping.hddss.cs.Agent_Server;

/**
 *
 * @author aliriosa
 */
public class HelloWorldServer extends Agent_Server{

    @Override
    public Object doService(Object arg) {

        System.out.println("HelloWorldServer received " + arg);
        return "Hello World";
        
    }

}
