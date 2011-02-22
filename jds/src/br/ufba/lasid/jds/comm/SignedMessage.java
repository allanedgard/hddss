/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

import java.security.PublicKey;
import java.security.SignedObject;

/**
 *
 * @author aliriosa
 */
public class SignedMessage implements IMessage{

    protected PublicKey key;
    protected SignedObject signedObject;

    public SignedMessage(){

    }

    public SignedMessage(SignedObject signedObject, PublicKey key){
        setPublicKey(key);
        setSignedObject(signedObject);
    }



    public PublicKey getPublicKey() {
        return key;
    }

    public void setPublicKey(PublicKey key) {
        this.key = key;        
    }

    public SignedObject getSignedObject() {
        return signedObject;
    }

    public void setSignedObject(SignedObject signedObject) {
        this.signedObject = signedObject;
    }
}
