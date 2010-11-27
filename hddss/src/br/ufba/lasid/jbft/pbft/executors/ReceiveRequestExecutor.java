/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft.executors;

import br.ufba.lasid.util.Executor;
import br.ufba.lasid.util.Protocol;
import br.ufba.lasid.util.actions.Action;
import br.ufba.lasid.jbft.pbft.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class ReceiveRequestExecutor extends Executor{

    public ReceiveRequestExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public void execute(Action act) {
        System.out.println("[ReceiveRequestExecutor] call ReceiveRequestExecutor.execute");
        PBFTMessage m = (PBFTMessage) act.getMessage();
        m.setType(PBFTMessage.TYPE.EXECUTE);
        protocol.doAction(m);
    }


}
