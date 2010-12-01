/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerServerExecuteExecutor;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
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
        
        if(!(m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.EXECUTE)))
            return;

        m.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.SENDREPLY);        
        m.setContent(getServer().doService(m.getContent()));


        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTServerExecuteExecutor.execute] call server.reply() for "
          + "execution of request " + m + " with result " + m.getContent()
          + " by server(p" + getProtocol().getLocalProcess().getID() + ") " 
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );

        getProtocol().doAction(m);
    }

    
}
