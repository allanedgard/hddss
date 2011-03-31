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
public class Protocol implements IProtocol{

    protected ICommunicator communicator = null;

    //public static String COMMUNICATOR   = "__ProtocolCommunicator";
    public static String TAG            = "protocol";

    public void setCommunicator(ICommunicator comm){

        communicator = comm;

    }

    public ICommunicator getCommunicator(){

        return communicator;

    }

}

