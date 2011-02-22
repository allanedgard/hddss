/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.security;

import br.ufba.lasid.jds.security.util.XSecurity;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignedObject;

/**
 *
 * @author aliriosa
 */
public class Authenticator implements IAuthenticator{

    protected String numberGeneratorAlgorithm = XSecurity.NUMGEN_SHA1PRNG;
    protected String keyGeneratorAlgorithm = XSecurity.KEYPAIRGEN_DSA;
    protected String encryptationAlgorithm = XSecurity.ENCRYPT_SHA1withDSA;
    
    protected String provider = XSecurity.PROVIDER_SUN;
    
    protected KeyPair keys;

    public Authenticator(String ngen, String kgen, String encAlg, String provider)
            throws NoSuchAlgorithmException, NoSuchProviderException
    {

        this.setKeyGeneratorAlgorithm(kgen);
        this.setNumberGeneratorAlgorithm(ngen);
        this.setEncryptationAlgorithm(encAlg);
        this.setProvider(provider);
        
        init();
    }

    public void init() 
            throws NoSuchAlgorithmException, NoSuchProviderException
    {
        keys = XSecurity.generateKeyPair(getKeyGeneratorAlgorithm(), getNumberGeneratorAlgorithm(), getProvider());
    }
    
    public KeyPair getKeys() {
        return keys;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }


    public String getNumberGeneratorAlgorithm() {
        return numberGeneratorAlgorithm;
    }

    public void setNumberGeneratorAlgorithm(String numberGeneratorAlgorithm) {
        this.numberGeneratorAlgorithm = numberGeneratorAlgorithm;
    }

    public String getKeyGeneratorAlgorithm() {
        return keyGeneratorAlgorithm;
    }

    public void setKeyGeneratorAlgorithm(String keyGeneratorAlgorithm) {
        this.keyGeneratorAlgorithm = keyGeneratorAlgorithm;
    }

    public String getEncryptationAlgorithm() {
        return encryptationAlgorithm;
    }

    public void setEncryptationAlgorithm(String encryptationAlgorithm) {
        this.encryptationAlgorithm = encryptationAlgorithm;
    }

    public SignedObject encrypt(Object data) throws Exception{
        return new SignedObject(
            (Serializable)data,
            keys.getPrivate(),
            XSecurity.getSignature(getEncryptationAlgorithm())
        );
    }


    public Object decrypt(SignedObject data) throws Exception{
        return data.getObject();
    }

    public PublicKey getPublicKey() {
        return keys.getPublic();
    }

    public boolean check(SignedObject data) {
        return XSecurity.check(data, getPublicKey(), getEncryptationAlgorithm());
    }

    public boolean check(SignedObject data, PublicKey key){
        return XSecurity.check(data, key, getEncryptationAlgorithm());
    }

    public Object makeDisgest(Object data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDisgest(Object data) throws Exception{
        return XSecurity.getDigest(data);
    }

    public boolean checkDisgest(Object data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
