/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors;

import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.acceptors.PBFTPrePrepareAcceptor;
import br.ufba.lasid.jds.jbft.pbft.acceptors.PBFTPrepareAcceptor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCollectorServant;

/**
 *
 * @author aliriosa
 */
public class PBFTPrepareCollectorServant extends PBFTCollectorServant<PBFTPrepare>{

    PBFTPrepareAcceptor acceptor;
    public PBFTPrepareCollectorServant(){

    }

    public PBFTPrepareCollectorServant(PBFT p){
        setProtocol(p);
        acceptor = new PBFTPrepareAcceptor(p);
    }


    protected synchronized boolean accept(PBFTPrepare prepare){
        return acceptor.accept(prepare);
    }

    public boolean canConsume(Object object) {

        if(object instanceof PBFTPrepare)
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
