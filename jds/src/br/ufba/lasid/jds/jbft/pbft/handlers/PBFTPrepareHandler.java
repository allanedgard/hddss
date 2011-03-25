/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;

/**
 *
 * @author aliriosa
 */
public class PBFTPrepareHandler extends PBFTServerMessageHandler{

    public PBFTPrepareHandler(IPBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTPrepare.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTPrepare) this.input);
        //}
    }

}
