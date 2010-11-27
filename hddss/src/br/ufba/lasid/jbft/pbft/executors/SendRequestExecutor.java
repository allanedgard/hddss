/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft.executors;

import br.ufba.lasid.jbft.Executor;
import br.ufba.lasid.jbft.Group;
import br.ufba.lasid.jbft.Message;
import br.ufba.lasid.jbft.Process;
import br.ufba.lasid.jbft.Protocol;
import br.ufba.lasid.jbft.actions.Action;
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
        ((PBFTMessage) act.getMessage()).setType(PBFTMessage.TYPE.RECEIVEREQUEST);
        protocol.getCommunicator().multicast((Message) act.getMessage(), (Process) group);
    }


}
