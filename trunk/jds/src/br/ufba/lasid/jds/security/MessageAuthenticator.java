/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.security;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.SignedMessage;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignedObject;

/**
 *
 * @author aliriosa
 */
public class MessageAuthenticator extends Authenticator implements IMessageAuthenticator{

    public MessageAuthenticator(String ngen, String kgen, String encAlg, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        super(ngen, kgen, encAlg, provider);
    }

    public SignedMessage encrypt(IMessage data) throws Exception{
        SignedObject signedObject = super.encrypt(data);

        return new SignedMessage(signedObject, keys.getPublic());
    }

    public boolean check(SignedMessage m){
        try{
            return super.check(m.getSignedObject(), m.getPublicKey());
        }catch(Exception e){
            return false;
        }
    }



}
