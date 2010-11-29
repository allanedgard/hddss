/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerReceiveRequestExecutor;
import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveRequestExecutor extends ClientServerReceiveRequestExecutor{

    public PBFTReceiveRequestExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        if(isPrimary(getProtocol().getLocalProcess())){
            PBFTMessage pp = createPrePrepare((PBFTMessage)act.getMessage());
            getProtocol().getCommunicator().multicast(
                pp, (Group)getProtocol().getContext().get(PBFT.LOCALGROUP)
            );
        }
        
    }
    /*
     *  If the local process is the primary replica then it has to assign a
        sequence number for the request and create a preprepare message.
     */
    public PBFTMessage createPrePrepare(PBFTMessage request){
        request.setType(PBFTMessage.TYPE.PREPREPARE);
        return request;
    }
    public boolean isPrimary(Process p){
        return true;
    }



}
