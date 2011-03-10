/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.jbft.pbft.PBFTClient;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTClientMessageHandler  extends Thread implements IMessageHandler {
    IMessage input;
    PBFTClient protocol;
    public PBFTClientMessageHandler(PBFTClient protocol){
        this.protocol = protocol;
        setName(getClass().getSimpleName() + "[" + protocol.getLocalProcessID() +"]");
    }

    public IMessage getInput() {
        return input;
    }

    public void setInput(IMessage input) {
        this.input = input;
    }

    public PBFTClient getProtocol() {
        return protocol;
    }

    public void setProtocol(PBFTClient protocol) {
        this.protocol = protocol;
    }

    public void input(IMessage obj){
        this.input = obj;
    }

    @Override
    public void run() {
        handle();
    }

}
