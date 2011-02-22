/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm.communicators;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.group.IGroup;

/**
 *
 * @author aliriosa
 */
public interface ICommunicator {
    
   public static String TAG = "communicator";
   
   public void multicast(IMessage m, IGroup g);
   public void unicast(IMessage m, IProcess p);   
   public void receive(IMessage m);

}
