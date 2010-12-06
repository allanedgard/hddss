/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.cs.executors.ClientServerCreateRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequestMessage;
import br.ufba.lasid.jds.security.Authenticator;

/**
 *
 * @author aliriosa
 */
public class PBFTCreateRequestExecutor extends ClientServerCreateRequestExecutor{

    public PBFTCreateRequestExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        Authenticator authenticator =
            ((PBFT)getProtocol()).getClientMessageAuthenticator();

        PBFTMessage m = new PBFTRequestMessage();
        
        m.put(PBFTMessage.PAYLOADFIELD, act.getWrapper());
        m.put(PBFTMessage.SOURCEFIELD, getProtocol().getLocalProcess());
        m.put(PBFTMessage.DESTINATIONFIELD, getProtocol().getRemoteProcess());

        Long timestamp =  ((PBFT)getProtocol()).getTimestamp();

        m.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.SENDREQUEST);
        m.put(PBFTMessage.TIMESTAMPFIELD, timestamp);
        m.put(PBFTMessage.CLIENTFIELD, getProtocol().getLocalProcess());


        m = (PBFTMessage)authenticator.encrypt(m);

        System.out.println(
            "client [p" + getProtocol().getLocalProcess().getID()+"] "
          + "create request with payload " + m.getContent() + " "
          + "at time " + ((PBFT)getProtocol()).getTimestamp()
       );
        
        getProtocol().doAction(m);
  
        
    }



}
