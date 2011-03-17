/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public abstract class MessageHandler extends Thread implements IMessageHandler  {
    protected IMessage input;
    protected Object lock;

    public Object getLock(Object obj, String name, Class _class){
        lock = null;
        try {
            lock = obj.getClass().getMethod(name, _class);
        } catch (Exception ex) {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lock;

    }

//    public IMessage getInput() {
//        return input;
//    }
//
//    public void setInput(IMessage input) {
//        this.input = input;
//    }

    public void input(IMessage obj){
        this.input = obj;
    }

    @Override
    public void run() {
        handle();
    }

}
