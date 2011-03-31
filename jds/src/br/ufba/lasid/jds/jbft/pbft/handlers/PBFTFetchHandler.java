/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetch;

/**
 *
 * @author aliriosa
 */
public class PBFTFetchHandler extends PBFTServerMessageHandler {

    public PBFTFetchHandler(IPBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTFetch.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTFetch) this.input);
        //}
    }

}
