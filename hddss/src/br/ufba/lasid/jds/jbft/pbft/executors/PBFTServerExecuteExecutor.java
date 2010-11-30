/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerServerExecuteExecutor;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTServerExecuteExecutor extends ClientServerServerExecuteExecutor{

    public PBFTServerExecuteExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage)act.getMessage();

        m.setContent(getServer().doService(m.getContent()));

        m.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.SENDREPLY);

        getProtocol().doAction(m);
    }

    
}
