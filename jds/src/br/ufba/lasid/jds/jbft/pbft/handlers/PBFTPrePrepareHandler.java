/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;

/**
 *
 * @author aliriosa
 */
public class PBFTPrePrepareHandler extends PBFTServerMessageHandler{

    public PBFTPrePrepareHandler(IPBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTPrePrepare.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTPrePrepare) this.input);
        //}
    }


}
