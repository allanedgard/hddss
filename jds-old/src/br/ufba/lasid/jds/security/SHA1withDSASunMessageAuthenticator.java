/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.security;

import br.ufba.lasid.jds.security.util.XSecurity;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 *
 * @author aliriosa
 */
public class SHA1withDSASunMessageAuthenticator extends MessageAuthenticator{

    public SHA1withDSASunMessageAuthenticator()
            throws NoSuchAlgorithmException, NoSuchProviderException {
        super(
                XSecurity.NUMGEN_SHA1PRNG,
                XSecurity.KEYPAIRGEN_DSA,
                XSecurity.ENCRYPT_SHA1withDSA,
                XSecurity.PROVIDER_SUN
        );
        
    }


}
