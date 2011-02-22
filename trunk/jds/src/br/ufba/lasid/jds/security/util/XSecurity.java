/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.security.util;

import trash.br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.util.XObject;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class XSecurity {

    public static final String DIGEST_MD2               = "MD2";
    public static final String DIGEST_MD5               = "MD5";
    public static final String DIGEST_SHA1              = "SHA-1";
    public static final String DIGEST_SHA256            = "SHA-256";
    public static final String DIGEST_SHA384            = "SHA-384";
    public static final String DIGEST_SHA512            = "SHA-512";

    public static final String ENCRYPT_NONEwithRSA      = "NONEwithRSA";
    public static final String ENCRYPT_MD2withRSA       = "MD2withRSA";
    public static final String ENCRYPT_MD5withRSA       = "MD5withRSA";
    public static final String ENCRYPT_SHA1withRSA      = "SHA1withRSA ";
    public static final String ENCRYPT_SHA256withRSA    = "SHA256withRSA";
    public static final String ENCRYPT_SHA384withRSA    = "SHA384withRSA";
    public static final String ENCRYPT_SHA512withRSA    = "SHA512withRSA";
    public static final String ENCRYPT_NONEwithDSA      = "NONEwithDSA";
    public static final String ENCRYPT_SHA1withDSA      = "SHA1withDSA";
    public static final String ENCRYPT_NONEwithECDSA    = "NONEwithECDSA";
    public static final String ENCRYPT_SHA1withECDSA    = "SHA1withECDSA";
    public static final String ENCRYPT_SHA256withECDSA  = "SHA256withECDSA";
    public static final String ENCRYPT_SHA384withECDSA  = "SHA384withECDSA";
    public static final String ENCRYPT_SHA512withECDSA  = "SHA512withECDSA";

    public static final String KEYPAIRGEN_DSA           = "DSA";
    public static final String KEYPAIRGEN_RSA           = "RSA";
    public static final String KEYPAIRGEN_EC            = "EC";
    public static final String KEYPAIRGEN_DiffieHellman = "DiffieHellman";
    
    public static final String KEYGEN_AES               = "AES";
    public static final String KEYGEN_ARCFOUR           = "ARCFOUR";
    public static final String KEYGEN_Blowfish          = "Blowfish";
    public static final String KEYGEN_DES               = "DES";
    public static final String KEYGEN_DESede            = "DESede";
    public static final String KEYGEN_HmacMD5           = "HmacMD5";
    public static final String KEYGEN_HmacSHA1          = "HmacSHA1";
    public static final String KEYGEN_HmacSHA256        = "HmacSHA256";
    public static final String KEYGEN_HmacSHA384        = "HmacSHA384";
    public static final String KEYGEN_HmacSHA512        = "HmacSHA512";
    public static final String KEYGEN_RC2               = "RC2";

    public static final String NUMGEN_SHA1PRNG          = "SHA1PRNG";
    
    public static final String PROVIDER_SUN     = "SUN";


    /**************************************************************************
     * DIGEST's Utility Methods.
     **************************************************************************/
    /**
     * Gerenates a message digest from a specified array of bytes, using a
     * defined algorithm.
     * @param algname -- name of the message digest algorithm.
     * @param ibytes -- the specified array of bytes
     * @return the message digest.
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getDigest(String algname, byte[] ibytes) 
            throws NoSuchAlgorithmException
    {
        MessageDigest md =
                MessageDigest.getInstance(algname);

        md.update(ibytes);

        return md.digest();
    }
    
    /**
     * Gerenates a message digest from a specified array of bytes, using DIGEST_MD5
     * algorithm.
     * @param ibytes -- the specified array of bytes
     * @return the message digest.
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getDigest(byte[] ibytes)
            throws NoSuchAlgorithmException
    {

        return getDigest(DIGEST_MD5, ibytes);
        
    }
    
    /**
     * Gerenates string message digest from a specified object, using a defined
     * algorithm.
     * @param algname -- name of the message digest algorithm.
     * @param obj -- the specified object
     * @return the message digest in hex format.
     * @throws NoSuchAlgorithmException
     */
    public static String getDigest(String algname, Object obj)
            throws NoSuchAlgorithmException, ClassNotFoundException, IOException
    {

        byte[] ibytes = XObject.objectToByteArray(obj);
        byte[] obytes = getDigest(algname, ibytes);

        return XObject.byteArrayToHexString(obytes);
        
    }

    /**
     * Gerenates string message digest from a specified object, using the DIGEST_MD5
     * algorithm.
     * @param obj -- the specified object
     * @return the message digest in hex format.
     * @throws NoSuchAlgorithmException
     */
    public static String getDigest(Object obj)
            throws NoSuchAlgorithmException, ClassNotFoundException, IOException
    {
        return getDigest(DIGEST_MD5, obj);
    }

    public static boolean check(SignedObject signedObj, PublicKey key, String algorithm){
        try {

            return signedObj.verify(key, getSignature(algorithm));

        } catch (Exception ex) {
            Logger.getLogger(XSecurity.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
        
    }

    public static Signature getSignature(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException{
        return Signature.getInstance(algorithm);
    }

    public static Signature getSignature(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException{
        return Signature.getInstance(algorithm, provider);
    }
    public static boolean check(SignedObject signedObj, PublicKey key){

        return check(signedObj, key, ENCRYPT_SHA1withDSA);
    }
    
    public static SignedMessage sign(Message m, KeyPair key)
            throws  IOException,
                    InvalidKeyException,
                    SignatureException,
                    NoSuchAlgorithmException,
                    NoSuchProviderException
    {
        return sign(m, key, ENCRYPT_SHA1withDSA);
    }
    public static SignedMessage sign(Message m, KeyPair key, String algorithm) 
            throws  IOException,
                    InvalidKeyException,
                    SignatureException,
                    NoSuchAlgorithmException,
                    NoSuchProviderException
    {
            SignedObject signedObj =
                    new SignedObject(
                            m, key.getPrivate(), getSignature(algorithm)
                    );

            SignedMessage sm = new SignedMessage();
            sm.setPublicKey(key.getPublic());
            sm.setSignedObject(signedObj);

            return sm;
    }

    public static boolean check(SignedMessage m){

        return check(m, ENCRYPT_SHA1withDSA);
    }
    
    public static boolean check(SignedMessage m, String algorithm){
        try{
            return check(m.getSignedObject(), (PublicKey)m.getPublicKey(), algorithm);
        }finally{
            return false;
        }
    }

    public static Message unsign(SignedMessage m){
        return null;
    }

 public static KeyPair generateKeyPair()
         throws NoSuchAlgorithmException, NoSuchProviderException
 {
     return generateKeyPair(KEYPAIRGEN_DSA, NUMGEN_SHA1PRNG, "SUN");
 }
 public static KeyPair generateKeyPair(String kgen, String ngen, String provider)
         throws NoSuchAlgorithmException, NoSuchProviderException
 {
        final KeyPairGenerator keyGen;
        final SecureRandom random;

        keyGen = KeyPairGenerator.getInstance(KEYPAIRGEN_DSA, provider);
        random = SecureRandom.getInstance(NUMGEN_SHA1PRNG, provider);

        // generate keys
        keyGen.initialize(1024, random);
        return keyGen.generateKeyPair();
   }

 
    public static void main(String[] args) throws Exception{

    }

}
