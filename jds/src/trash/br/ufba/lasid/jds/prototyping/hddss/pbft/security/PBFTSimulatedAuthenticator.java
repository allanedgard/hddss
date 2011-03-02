/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.prototyping.hddss.pbft.security;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.security.IAuthenticator;
import trash.br.ufba.lasid.jds.security.ISecurityKey;
import java.security.PublicKey;
import java.security.SignedObject;

/**
 *
 * @author aliriosa
 */
public class PBFTSimulatedAuthenticator implements IAuthenticator<PBFTMessage>{

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
        //data.put(AUTHENTICATORFIELD, generateAuthentication(data));
        return data;
    }

    public PBFTMessage decrypt(PBFTMessage data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public boolean check(PBFTMessage data) {
        Object auth = null;//data.get(AUTHENTICATORFIELD);
        if(auth == null)
            return false;
        return ((Boolean)auth);
    }

    public PBFTMessage makeDisgest(PBFTMessage data) {
        //data.put(PBFTMessage.DIGESTFIELD, generateDisgest(data));
        return data;

    }

    public boolean generateAuthentication(PBFTMessage m){
        return true;
    }

    public String generateDisgest(PBFTMessage m){
        return "AAAAAAAAAAAA";
    }

    public boolean checkDisgest(PBFTMessage data) {
        String digest = generateDisgest(data);
        return false;//digest.equals(data.get(PBFTMessage.DIGESTFIELD));
    }

    public String getDigest(PBFTMessage data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SignedObject encrypt(Object data) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object decrypt(SignedObject data) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PublicKey getPublicKey() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean check(SignedObject data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
