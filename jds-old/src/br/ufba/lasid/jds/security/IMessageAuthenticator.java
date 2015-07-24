/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.security;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.SignedMessage;

/**
 *
 * @author aliriosa
 */
public interface IMessageAuthenticator extends IAuthenticator{

    public SignedMessage encrypt(IMessage data) throws Exception;
    public boolean check(SignedMessage m);

}
