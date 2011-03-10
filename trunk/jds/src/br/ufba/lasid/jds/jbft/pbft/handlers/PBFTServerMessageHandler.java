/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTServerMessageHandler extends Thread implements IMessageHandler {
    IMessage input;
    PBFTServer protocol;
    protected Object lock;

    public Object getLock(String name, Class _class){
        lock = null;
        try {
            lock = getProtocol().getClass().getMethod(name, _class);
        } catch (Exception ex) {
            Logger.getLogger(PBFTServerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lock;

    }
    public PBFTServerMessageHandler(PBFTServer protocol){
        this.protocol = protocol;
    }

    public IMessage getInput() {
        return input;
    }

    public void setInput(IMessage input) {
        this.input = input;
    }

    public PBFTServer getProtocol() {
        return protocol;
    }

    public void setProtocol(PBFTServer protocol) {
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
