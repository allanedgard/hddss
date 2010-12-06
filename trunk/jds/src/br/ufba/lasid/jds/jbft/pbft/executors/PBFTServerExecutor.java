/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.security.Authenticator;

/**
 *
 * @author aliriosa
 */
public class PBFTServerExecutor extends Executor{

    public PBFTServerExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    public boolean isServerAuthenticated(PBFTMessage m){
        Authenticator authenticator =
            ((PBFT)getProtocol()).getServerAuthenticator();

        return authenticator.check(m);
    }


    public PBFTMessage encrypt(PBFTMessage m){
        Authenticator authenticator =
                ((PBFT)getProtocol()).getServerAuthenticator();

        return (PBFTMessage)authenticator.encrypt(m);
        
    }
    public String getDefaultSecurityExceptionMessage(PBFTMessage m, String act){
            return (
                "server [p" + getProtocol().getLocalProcess().getID()+"] "
              + "cann't execute " + act + " because the request  " + m.getContent() + " "
              + "wasn't authenticated. "
            );
        
    }

    public PBFTMessage makeDisgest(PBFTMessage m){

        Authenticator authenticator =
                ((PBFT)getProtocol()).getServerAuthenticator();

        return (PBFTMessage)authenticator.makeDisgest(m);
        
    }

    public boolean checkDigest(PBFTMessage m){
        Authenticator authenticator =
                ((PBFT)getProtocol()).getServerAuthenticator();

        return authenticator.chechDisgest(m);
        
    }
}
