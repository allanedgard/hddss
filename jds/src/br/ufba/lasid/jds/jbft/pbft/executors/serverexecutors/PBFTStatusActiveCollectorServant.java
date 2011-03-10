/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors;

import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.acceptors.PBFTStatusActiveAcceptor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCollectorServant;

/**
 *
 * @author aliriosa
 */
public class PBFTStatusActiveCollectorServant extends PBFTCollectorServant<PBFTStatusActive>{

    PBFTStatusActiveAcceptor acceptor;
    public PBFTStatusActiveCollectorServant(){

    }

    public PBFTStatusActiveCollectorServant(PBFT p){
        setProtocol(p);
        acceptor = new PBFTStatusActiveAcceptor(p);
    }

    protected synchronized boolean accept(PBFTStatusActive statusActive){
        return acceptor.accept(statusActive);
    }


    public synchronized boolean canConsume(Object object) {

        if(object instanceof PBFTStatusActive)
            return true;

        if(object instanceof PDU){
            PDU pdu = (PDU) object;
            return canConsume(pdu.getPayload());
        }

        if(object instanceof SignedMessage){
            try{

                SignedMessage m = (SignedMessage) object;

                return canConsume(m.getSignedObject().getObject());

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        return false;
    }

}
