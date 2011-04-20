/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

/**
 *
 * @author aliriosa
 */
public class DistributedProtocol extends Protocol implements IDistributedProtocol{
    
//    public static String PROCESS        = "__ProtocolProcess";
//    public static String REMOTEPROCESS  = "__RemoteProcess";

    protected IProcess local;
    protected ISystemEntity remote;
    
    public IProcess getLocalProcess() {
        return local;
    }

    public void setLocalProcess(IProcess process) {
        local = process;
    }

    public ISystemEntity getRemoteProcess() {
        return remote;
    }

    public void setRemoteProcess(ISystemEntity process) {
        remote = process;
    }

}

