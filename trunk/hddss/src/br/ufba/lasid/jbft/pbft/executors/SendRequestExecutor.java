/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft.executors;

import br.ufba.lasid.util.Executor;
import br.ufba.lasid.util.Group;
import br.ufba.lasid.util.Message;
import br.ufba.lasid.util.Process;
import br.ufba.lasid.util.Protocol;
import br.ufba.lasid.util.actions.Action;
import br.ufba.lasid.jbft.pbft.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class SendRequestExecutor extends Executor{
    Group group;
    

    public SendRequestExecutor(Protocol protocol) {
        super(protocol);
    }


    @Override
    public void execute(Action act) {
        System.out.println("[SendRequestExecutor] call SendRequestExecutor.execute");
        ((PBFTMessage) act.getMessage()).setType(PBFTMessage.TYPE.RECEIVEREQUEST);
        
        protocol.getCommunicator().multicast((Message) act.getMessage(), (Process) group);
    }


}
