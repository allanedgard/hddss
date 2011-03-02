/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.security;

import java.security.PublicKey;
import java.security.SignedObject;

/**
 *
 * @author aliriosa
 */
public interface IAuthenticator<T> {
    public SignedObject encrypt(Object data) throws Exception;
    public Object decrypt(SignedObject data) throws Exception;

    public PublicKey getPublicKey();

    public boolean check(SignedObject data);

    public T makeDisgest(T data);

    public String getDigest(T data) throws Exception;

    public boolean checkDisgest(T data);

}
