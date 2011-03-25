/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTProcessingToken;

/**
 *
 * @author aliriosa
 */
public class PBFTProcessingHandler extends PBFTServerMessageHandler {

    public PBFTProcessingHandler(IPBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTProcessingToken.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTProcessingToken) this.input);
        //}
    }

}
