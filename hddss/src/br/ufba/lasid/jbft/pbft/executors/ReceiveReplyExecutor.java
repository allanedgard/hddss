/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft.executors;

import br.ufba.lasid.jbft.Executor;
import br.ufba.lasid.jbft.Protocol;
import br.ufba.lasid.jbft.actions.Action;
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
        PBFTMessage m = (PBFTMessage) act.getMessage();

        System.out.println(m.toString());
    }


}
