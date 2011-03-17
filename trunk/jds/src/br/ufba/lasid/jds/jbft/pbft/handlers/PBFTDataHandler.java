/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTData;

/**
 *
 * @author aliriosa
 */
class PBFTDataHandler extends PBFTServerMessageHandler {

    public PBFTDataHandler(PBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTData.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTData) this.input);
        //}
    }

}