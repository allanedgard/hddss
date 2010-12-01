/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.security;

/**
 *
 * @author aliriosa
 */
public interface Authenticator<T> {
    public T encrypt(T data);
    
    public T decrypt(T data);

    public void setKey(SecurityKey key);

    public SecurityKey getKey();

    public boolean check(T data);

    public T makeDisgest(T data);

    public boolean chechDisgest(T data);

}
