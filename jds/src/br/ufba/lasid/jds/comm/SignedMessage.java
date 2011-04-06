/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

import java.io.IOException;
import java.security.PublicKey;
import java.security.SignedObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class SignedMessage implements IMessage{

    protected transient PublicKey key;
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

    @Override
    public String toString() {
        Object obj = null;
        try {
            obj = signedObject.getObject();
        } catch (IOException ex) {
            Logger.getLogger(SignedMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SignedMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "SignedMessage{" + obj + '}';
    }


}
