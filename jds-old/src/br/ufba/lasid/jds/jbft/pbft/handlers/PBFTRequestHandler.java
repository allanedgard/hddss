/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;

/**
 *
 * @author aliriosa
 */
public class PBFTRequestHandler extends PBFTServerMessageHandler{    

    public PBFTRequestHandler(IPBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTRequest.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTRequest) this.input);
        //}
    }

}
