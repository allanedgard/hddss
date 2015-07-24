/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewACK;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;

/**
 *
 * @author aliriosa
 */
class PBFTChangeViewACKHandler extends PBFTServerMessageHandler{

    public PBFTChangeViewACKHandler(IPBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTChangeViewACK.class);
    }

    public void handle() {
      getProtocol().handle((PBFTChangeViewACK) this.input);
    }
}
