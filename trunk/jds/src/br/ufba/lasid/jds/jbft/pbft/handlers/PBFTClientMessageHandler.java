/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.comm.MessageHandler;
import br.ufba.lasid.jds.jbft.pbft.client.PBFTClient;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTClientMessageHandler  extends MessageHandler {
    PBFTClient protocol;
    public PBFTClientMessageHandler(PBFTClient protocol){
        this.protocol = protocol;
        setName(getClass().getSimpleName() + "[" + protocol.getLocalProcessID() +"]");
    }

    public PBFTClient getProtocol() {
        return protocol;
    }

    public void setProtocol(PBFTClient protocol) {
        this.protocol = protocol;
    }

}
