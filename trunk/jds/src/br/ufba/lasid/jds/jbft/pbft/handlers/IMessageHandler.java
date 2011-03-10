/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.comm.IMessage;

/**
 *
 * @author aliriosa
 */
interface IMessageHandler {
    public void input(IMessage message);
    public void handle();
    public void start();
    public void join() throws InterruptedException;
}
