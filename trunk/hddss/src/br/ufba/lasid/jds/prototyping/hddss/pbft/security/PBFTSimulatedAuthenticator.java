/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft.security;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.security.Authenticator;
import br.ufba.lasid.jds.security.Digest;
import br.ufba.lasid.jds.security.SecurityKey;

/**
 *
 * @author aliriosa
 */
public class PBFTSimulatedAuthenticator implements Authenticator<PBFTMessage>{

    public String AUTHENTICATORFIELD =
            "PBFTSimulatedAuthenticator";

    public PBFTSimulatedAuthenticator() {
    }

    public PBFTSimulatedAuthenticator(String tag) {
        setTAG(tag);
    }


    public void setTAG(String tag){
        AUTHENTICATORFIELD += tag;
    }

    public PBFTMessage encrypt(PBFTMessage data) {
        data.put(AUTHENTICATORFIELD, generateAuthentication());
        return data;
    }

    public PBFTMessage decrypt(PBFTMessage data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setKey(SecurityKey key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SecurityKey getKey() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean check(PBFTMessage data) {
        return ((Boolean)(data.get(AUTHENTICATORFIELD)));
    }

    public Digest digest(PBFTMessage data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean generateAuthentication(){
        return true;
    }

}
