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
public class ReceiveReplyExecutor extends Executor{

    public ReceiveReplyExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public void execute(Action act) {
        System.out.println("[ReceiveReplyExecutor] call ReceiveReplyExecutor.execute");
        PBFTMessage m = (PBFTMessage) act.getMessage();

        System.out.println(m.toString());
    }


}
