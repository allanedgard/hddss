/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors;

import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.acceptors.PBFTBagAcceptor;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTExecutorBroker;

/**
 *
 * @author aliriosa
 */
public class PBFTBagBrokerServant extends PBFTExecutorBroker<PBFTBag, PBFTCommit>{

    PBFTBagAcceptor acceptor;

    public PBFTBagBrokerServant(){

    }

    public PBFTBagBrokerServant(PBFT p){
        setProtocol(p);
        acceptor = new PBFTBagAcceptor(p);
        acceptor.setSupplier(this);
    }

    protected synchronized boolean accept(PBFTBag bag){
        
        return acceptor.accept(bag);
                
    }

    public boolean canConsume(Object object) {

        if(object instanceof PBFTBag)
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

