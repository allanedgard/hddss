/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.ISystemEntity;

/**
 *
 * @author aliriosa
 */
public class DistributedProtocol extends Protocol{
    
//    public static String PROCESS        = "__ProtocolProcess";
//    public static String REMOTEPROCESS  = "__RemoteProcess";

    protected IProcess local;
    protected ISystemEntity remote;
    
    public synchronized IProcess getLocalProcess() {
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

