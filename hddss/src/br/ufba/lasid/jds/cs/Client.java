/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs;

import br.ufba.lasid.jds.Executor;

/**
 *
 * @author aliriosa
 */
public interface Client<T> extends br.ufba.lasid.jds.Process<T> {
    public void receiveReply(Object content);

    public Executor newClientServerSendRequestExecutor();
    public Executor newClientServerReceiveReplyExecutor();

}
