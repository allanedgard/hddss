/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeView;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;

/**
 *
 * @author aliriosa
 */
class PBFTChangeViewHandler extends PBFTServerMessageHandler{

    public PBFTChangeViewHandler(IPBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTChangeView.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTChangeView) this.input);
        //}
    }
}
