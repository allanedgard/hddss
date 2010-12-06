/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

/**
 *
 * @author aliriosa
 */
public class DistributedProtocol extends Protocol{
    
    public static String PROCESS        = "__ProtocolProcess";
    public static String REMOTEPROCESS  = "__RemoteProcess";
    
    public Process getLocalProcess() {
        return (Process)getContext().get(PROCESS);
    }

    public void setLocalProcess(Process process) {
        getContext().put(PROCESS, process);
    }

    public Process getRemoteProcess() {
        return (Process)getContext().get(REMOTEPROCESS);
    }

    public void setRemoteProcess(Process process) {
        getContext().put(REMOTEPROCESS, process);
    }

}

