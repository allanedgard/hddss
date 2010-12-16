/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTSendChangeViewExecutor extends PBFTServerExecutor{

    public PBFTSendChangeViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {

        PBFTMessage m = (PBFTMessage) act.getWrapper();

        m.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVECHANGEVIEW);
        
        Group g = ((PBFT)getProtocol()).getLocalGroup();

        getProtocol().getCommunicator().multicast(m, g);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID() + "] "
          + "multicasts change view message at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );
        
    }



}
