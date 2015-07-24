/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;

/**
 *
 * @author aliriosa
 */
public class PBFTBagHandler extends PBFTServerMessageHandler {

    public PBFTBagHandler(IPBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTBag.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTBag) this.input);
        //}
    }

}