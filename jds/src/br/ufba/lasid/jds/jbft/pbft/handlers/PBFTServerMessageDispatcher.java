/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.comm.IMessageHandler;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetch;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetchMetaData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMetaData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class PBFTServerMessageDispatcher extends PBFTServerMessageHandler {
    public PBFTServerMessageDispatcher(PBFTServer protocol){
        super(protocol);
    }

    @Override
    public void run() {
        handle();
    }

    public IMessage getMessage(){

        return extract(this.input);
    }

    public IMessage extract(IMessage m){
        
        if(m instanceof PDU){
            PDU pdu = (PDU) m;
            return extract((IMessage) pdu.getPayload());
        }

        if(m instanceof SignedMessage){
            SignedMessage signedMessage = (SignedMessage) m;
            try {
                if(getProtocol().getAuthenticator().check(signedMessage)){
                    return (IMessage) signedMessage.getSignedObject().getObject();
                }
                return null;
            } catch (Exception except) {
                Logger.getLogger(PBFTServerMessageDispatcher.class.getName()).log(Level.SEVERE, null, except);
                except.printStackTrace();
            }
        }

        return null;
    }

    public void handle() {
        
        IMessageHandler handler = null;

        IMessage m = getMessage();
        
        if(m instanceof PBFTRequest){
            handler = new PBFTRequestHandler(getProtocol());

        }

        if(m instanceof PBFTPrePrepare){
            handler = new PBFTPrePrepareHandler(getProtocol());
        }

        if(m instanceof PBFTPrepare){
            handler = new PBFTPrepareHandler(getProtocol());
        }

        if(m instanceof PBFTCommit){
            handler = new PBFTCommitHandler(getProtocol());
        }

        if(m instanceof PBFTCheckpoint){
           handler = new PBFTCheckpointHandler(getProtocol());
        }

        if(m instanceof PBFTStatusActive){
           handler = new PBFTStatusActiveHandler(getProtocol());
        }

        if(m instanceof PBFTBag){
            handler = new PBFTBagHandler(getProtocol());
        }

        if(m instanceof PBFTFetchMetaData){
            handler = new PBFTFetchMetaDataHandler(getProtocol());
        }

        if(m instanceof PBFTMetaData){
            handler = new PBFTMetaDataHandler(getProtocol());
        }
        
        if(m instanceof PBFTFetch){
            handler = new PBFTFetchHandler(getProtocol());
        }

        if(m instanceof PBFTData){
            handler = new PBFTDataHandler(getProtocol());
        }

        if(handler != null){
            handler.input(m);
            handler.handle();
            //handler.start();
        }
        
    }
    
}
