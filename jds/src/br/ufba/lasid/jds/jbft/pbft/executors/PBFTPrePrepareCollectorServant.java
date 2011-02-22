/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTTimeoutDetector;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import trash.br.ufba.lasid.jds.jbft.pbft.executors.PBFTCollectorServant;
import br.ufba.lasid.jds.util.Debugger;
import br.ufba.lasid.jds.util.StatedPBFTRequestMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTPrePrepareCollectorServant extends PBFTCollectorServant<PBFTPrePrepare>{


    public PBFTPrePrepareCollectorServant(){

    }

    public PBFTPrePrepareCollectorServant(PBFT p){
        setProtocol(p);
    }


    /**
     * Collect the request sent by the client.
     * @param preprepare -- the client request.
     */
    protected synchronized boolean accept(PBFTPrePrepare preprepare){

        PBFTServer pbft = (PBFTServer)getProtocol();

        /**
         * If the preprepare hasn't a valid sequence or view number then force a
         * change view.
         */
        if(!(pbft.hasAValidSequenceNumber(preprepare) && pbft.hasAValidViewNumber(preprepare))){
            Debugger.debug(
              "[PBFTPrePrepareCollectorServant] s"  + pbft.getLocalProcess().getID() +
              ", at time " + pbft.getClock().value() + ", discarded " + preprepare +
              " because it hasn't a valid sequence/view number. "
              + "(currView = " + pbft.getCurrentViewNumber() + ")"
              + "[nextPP = " + pbft.getNextPrePrepareSEQ() + ", nextP = "
              + pbft.getNextPrepareSEQ() + ", nextC =" + pbft.getNextCommitSEQ()
              + " , nextE = " + pbft.getNextExecuteSEQ() + "]"
            );

            return false;

        }

        /**
         * If the preprepare message wasn't sent by the primary replica then
         * it will be discarded.
         */
        if(!pbft.wasSentByPrimary(preprepare)){
            Debugger.debug(
              "[PBFTPrePrepareCollectorServant] s"   + pbft.getLocalProcess().getID()   +
              ", at time " + pbft.getClock().value() + ", discarded " + preprepare      +
              " because it wasn't sent by primary server s" + pbft.getCurrentPrimaryID()
            );

            return false;
        }

        if(pbft.updateState(preprepare)){

            /**
             * For each request in batch, check if such request was received.
             */
            for(String digest : preprepare.getDigests()){

                StatedPBFTRequestMessage sr = pbft.getStateLog().getStatedRequest(digest);

                sr.setState(StatedPBFTRequestMessage.RequestState.PREPREPARED);
                sr.setSequenceNumber(preprepare.getSequenceNumber());
                
                revokeSchedule(digest);

                Debugger.debug(
                  "[PBFTServer] s"  + pbft.getLocalServerID() +
                  ", at time " + pbft.getClock().value() + ", revoked the timeout "
                + "for pre-prepare of " + sr.getRequest()
                );

            }

            pbft.updateNextPrePrepareSEQ(preprepare);
            
            if(!pbft.isPrimary()){
                emit(createPrepareMessage(preprepare));
            }

            return true;
        }

        return false;
            
    }

    public void revokeSchedule(String digest){

        PBFTServer pbft = (PBFTServer)getProtocol();

        PBFTTimeoutDetector timeoutTask =
            (PBFTTimeoutDetector)pbft.getTaskTable(PBFT.REQUESTTASKS).get(digest);
        
        if(timeoutTask != null){
            timeoutTask.cancel();
            pbft.getTaskTable(PBFT.REQUESTTASKS).remove(digest);
        }


    }

    protected PBFTPrepare createPrepareMessage(PBFTPrePrepare pp){

        PBFTServer pbft = (PBFTServer)getProtocol();

        PBFTPrepare p = new PBFTPrepare(pp, pbft.getLocalServerID());

        return p;
        
    }

    public synchronized void emit(PBFTPrepare p){
        PBFTServer pbft = (PBFTServer)getProtocol();

        SignedMessage m;

        try {

            m = pbft.getAuthenticator().encrypt(p);

            IGroup  g  = pbft.getLocalGroup();
            IProcess s = pbft.getLocalProcess();

            PDU pdu = new PDU();
            pdu.setSource(s);
            pdu.setDestination(g);
            pdu.setPayload(m);

            pbft.getCommunicator().multicast(pdu, g);

            Debugger.debug(
              "[PBFTPrePrepareCollectorServant]s" +  pbft.getLocalProcess().getID() +
              " sent prepare " + p + " at timestamp " + pbft.getClock().value() +
              " to group " + pbft.getLocalGroup() + "."
            );


        } catch (Exception ex) {
            Logger.getLogger(PBFTPrePrepareCollectorServant.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();

        }

    }




    public boolean canConsume(Object object) {

        if(object instanceof PBFTPrePrepare)
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

