/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.comm.MessageHandler;
import br.ufba.lasid.jds.jbft.pbft.IPBFTServer;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTServerMessageHandler extends MessageHandler {
    IPBFTServer protocol;

    public Object getLock(String name, Class _class){
        return super.getLock(getProtocol(), name, _class);
    }
    
    public PBFTServerMessageHandler(IPBFTServer protocol){
        this.protocol = protocol;
    }

    public IPBFTServer getProtocol() {
        return protocol;
    }

    public void setProtocol(IPBFTServer protocol) {
        this.protocol = protocol;
    }

}
