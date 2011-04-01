/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;

/**
 *
 * @author aliriosa
 */
class PBFTNewViewHandler extends PBFTServerMessageHandler{

    public PBFTNewViewHandler(IPBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTNewView.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTNewView) this.input);
        //}
    }
}
