/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusPending;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;

/**
 *
 * @author aliriosa
 */
public class PBFTStatusPendingHandler extends PBFTServerMessageHandler{

    public PBFTStatusPendingHandler(IPBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTStatusPending.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTStatusPending) this.input);
        //}
    }
}
