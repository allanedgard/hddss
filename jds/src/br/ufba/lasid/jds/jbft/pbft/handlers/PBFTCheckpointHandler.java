/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckpointHandler extends PBFTServerMessageHandler{

    public PBFTCheckpointHandler(PBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTCheckpoint.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTCheckpoint) this.input);
        //}
    }

}
