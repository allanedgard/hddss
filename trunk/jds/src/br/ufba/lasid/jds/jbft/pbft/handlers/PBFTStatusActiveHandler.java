/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;

/**
 *
 * @author aliriosa
 */
class PBFTStatusActiveHandler extends PBFTServerMessageHandler{

    public PBFTStatusActiveHandler(PBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTStatusActive.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTStatusActive) this.input);
        //}
    }
}
