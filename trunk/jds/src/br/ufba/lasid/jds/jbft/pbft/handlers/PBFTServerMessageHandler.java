/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageHandler;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTServerMessageHandler extends MessageHandler {
    PBFTServer protocol;

    public Object getLock(String name, Class _class){
        return super.getLock(getProtocol(), name, _class);
    }
    
    public PBFTServerMessageHandler(PBFTServer protocol){
        this.protocol = protocol;
    }

    public PBFTServer getProtocol() {
        return protocol;
    }

    public void setProtocol(PBFTServer protocol) {
        this.protocol = protocol;
    }

}
