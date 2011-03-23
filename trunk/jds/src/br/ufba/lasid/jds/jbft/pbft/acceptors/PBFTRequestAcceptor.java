/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.acceptors;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.util.JDSUtility;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class PBFTRequestAcceptor extends PBFTAcceptor<PBFTRequest>{
    
    public PBFTRequestAcceptor(PBFT pbft) {
        super(pbft);
    }

    public synchronized boolean accept(PBFTRequest request) {

        PBFTServer pbft = (PBFTServer) getProtocol();

        if(request == null){
            JDSUtility.debug(
              "[PBFTRequestAcceptor:accept(request)] s" + pbft.getLocalServerID() +
              " didn't accept " + request + ". (time = " + pbft.getClockValue() + ")"
            );
            return false;
        }

        if(!pbft.isNextRequest(request)){
            JDSUtility.debug(
              "[PBFTRequestAcceptor:accept(request)] s" + pbft.getLocalServerID() +
              " didn't accept " + request + " because this is out of order."
            );

            return false;
        }

        IProcess client = new br.ufba.lasid.jds.BaseProcess(request.getClientID());

        /**
         * Check if request was already accepted.
         */
        if(pbft.getStateLog().wasAccepted(request)){

            /**
             * Check if request was already served.
             */
            if(pbft.getStateLog().wasServed(request)){
                JDSUtility.debug(
                  "[PBFTRequestAcceptor:accept(request)] s" + pbft.getLocalServerID() +
                  " has already served " + request + "."
                );

                /**
                 * Retransmite the reply for a request that had been already
                 * served.
                 */
                pbft.emit(pbft.getStateLog().getReplyInRequestTable(request), client);

                return false;

            }

            JDSUtility.debug(
              "[PBFTRequestAcceptor:accept(request)] s" + pbft.getLocalServerID() +
              " has already accepted " + request + " so this was discarded."
            );

            return false;

        }

        /**
         * If the reply there is no more in the current state then PBFT2'll send
         * a reply if null payload.
         */
        if(pbft.getStateLog().noMore(request)){
            JDSUtility.debug(
              "[PBFTRequestAcceptor:accept(request)] s" + pbft.getLocalServerID() +
              " hasn't a response for  " + request + " any more."
            );

            pbft.emit(pbft.createNullReplyMessage(request), client);
            return false;

        }

        String digest = null;

        try{
            digest = pbft.getAuthenticator().getDigest(request);

        } catch (Exception ex) {

            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            return false;

        }

        JDSUtility.debug(
          "[PBFTRequestAcceptor:accept(request)] s" + pbft.getLocalServerID() +
          " accepted " + request + " at time " + pbft.getClockValue() + "."
        );


        /**
         * If a request is new then it will be accepted and put it in back log
         * state.
         */
        pbft.getStateLog().insertRequestInTables(
            digest, request, StatedPBFTRequestMessage.RequestState.WAITING
        );

        pbft.updateClientSession(request);

        /**
         * Perform the batch procedure if the server is the primary replica.
         */
        if(pbft.isPrimary()){
            JDSUtility.debug(
              "[PBFTRequestAcceptor:accept(request)] s" + pbft.getLocalServerID() + " (primary)" +
              " is executing the batch procedure for " + request + "."
            );

            pbft.batch(digest);
            return true;

        }

        /**
         * Schedule a timeout for the arriving of the pre-prepare message if
         * the server is a secundary replica.
         */
        pbft.scheduleViewChange(digest);

        return true;

    }

}
