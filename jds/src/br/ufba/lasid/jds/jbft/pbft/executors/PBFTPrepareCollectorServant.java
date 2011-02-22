/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
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
public class PBFTPrepareCollectorServant extends PBFTCollectorServant<PBFTPrepare>{


    public PBFTPrepareCollectorServant(){

    }

    public PBFTPrepareCollectorServant(PBFT p){
        setProtocol(p);
    }


    /**
     * Collect the request sent by the client.
     * @param prepare -- the client request.
     */
    protected synchronized boolean accept(PBFTPrepare prepare){

        PBFTServer pbft = (PBFTServer)getProtocol();

        /**
         * If the preprepare hasn't a valid sequence or view number then force a
         * change view.
         */
        if(!(pbft.hasAValidSequenceNumber(prepare) && pbft.hasAValidViewNumber(prepare))){
            Debugger.debug(
              "[PBFTPrepareCollectorServant] s"  + pbft.getLocalProcess().getID() +
              ", at time " + pbft.getClock().value() + ", discarded " + prepare +
              " because it hasn't a valid sequence/view number. "
              + "(currView = " + pbft.getCurrentViewNumber() + ")"
              + "[nextPP = " + pbft.getNextPrePrepareSEQ() + ", nextP = "
              + pbft.getNextPrepareSEQ() + ", nextC =" + pbft.getNextCommitSEQ()
              + " , nextE = " + pbft.getNextExecuteSEQ() + "]"
            );

            return false;

        }

        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        if(!pbft.wasSentByAGroupMember(prepare)){
            Debugger.debug(
              "[PBFTPrepareCollectorServant] s"   + pbft.getLocalProcess().getID()   +
              ", at time " + pbft.getClock().value() + ", discarded " + prepare      +
              " because it wasn't sent by a member of the group " + pbft.getLocalGroup()
            );

            return false;
        }

        if(!pbft.getStateLog().wasPrePrepared(prepare)){
            Debugger.debug(
              "[PBFTPrepareCollectorServant] s"   + pbft.getLocalProcess().getID()   +
              ", at time " + pbft.getClock().value() + ", discarded " + prepare      +
              " because it hasn't received a related pre-prepare."
            );

            return false;
        }

        if(pbft.updateState(prepare)){
            
            Long seqn = prepare.getSequenceNumber();
            
            Quorum q  = pbft.getStateLog().getPrepareQuorum(seqn);

            if(q != null && q.complete()){

                for(String digest : prepare.getDigests()){
                    
                    StatedPBFTRequestMessage statedReq =
                            pbft.getStateLog().getStatedRequest(digest);

                    statedReq.setState(
                            StatedPBFTRequestMessage.RequestState.PREPARED
                    );

                    statedReq.setSequenceNumber(prepare.getSequenceNumber());
                }
                Debugger.debug(
                  "[PBFTPrepareCollectorServant] s" + pbft.getLocalServerID()     +
                  ", at time " + pbft.getClock().value() + ", has just complete " +
                  "the prepare phase for sequence number (" + seqn + ") and "     +
                  "view number (" + prepare.getViewNumber() + ")."
                );

                pbft.updateNextPrepareSEQ(prepare);
                
                emit(createCommitMessage(prepare));
                
            }
            return true;
        }

        return false;

    }


    protected PBFTCommit createCommitMessage(PBFTPrepare p){

        PBFTServer pbft = (PBFTServer)getProtocol();

        PBFTCommit c = new PBFTCommit(p, pbft.getLocalServerID());

        return c;

    }

    public synchronized void emit(PBFTCommit c){
        PBFTServer pbft = (PBFTServer)getProtocol();

        SignedMessage m;

        try {

            m = pbft.getAuthenticator().encrypt(c);

            IGroup  g  = pbft.getLocalGroup();
            IProcess s = pbft.getLocalProcess();

            PDU pdu = new PDU();
            pdu.setSource(s);
            pdu.setDestination(g);
            pdu.setPayload(m);

            pbft.getCommunicator().multicast(pdu, g);

            Debugger.debug(
              "[PBFTPrepareCollectorServant]s" +  pbft.getLocalProcess().getID() +
              " sent commit " + c + " at timestamp " + pbft.getClock().value() +
              " to group " + pbft.getLocalGroup() + "."
            );


        } catch (Exception ex) {
            Logger.getLogger(PBFTPrepareCollectorServant.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

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
