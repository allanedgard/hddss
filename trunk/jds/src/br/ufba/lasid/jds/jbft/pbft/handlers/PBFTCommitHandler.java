/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;

/**
 *
 * @author aliriosa
 */
public class PBFTCommitHandler extends PBFTServerMessageHandler {

    public PBFTCommitHandler(PBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTCommit.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTCommit) this.input);
        //}
    }

}
