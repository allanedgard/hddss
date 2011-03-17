/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMetaData;

/**
 *
 * @author aliriosa
 */
public class PBFTMetaDataHandler extends PBFTServerMessageHandler {

    public PBFTMetaDataHandler(PBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTMetaData.class);
    }

    public void handle() {
        // synchronized(lock){
            getProtocol().handle((PBFTMetaData) this.input);
        //}
    }

}
