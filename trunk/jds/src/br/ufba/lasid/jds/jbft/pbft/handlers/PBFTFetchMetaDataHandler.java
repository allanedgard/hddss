/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetchMetaData;

/**
 *
 * @author aliriosa
 */
public class PBFTFetchMetaDataHandler extends PBFTServerMessageHandler {

    public PBFTFetchMetaDataHandler(PBFTServer protocol) {
        super(protocol);
        lock = getLock("handle", PBFTFetchMetaData.class);
    }

    public void handle() {
        // synchronized(lock){
        //            getProtocol().handle((PBFTFetchMetaData) this.input);
        //}
    }

}
