/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.acceptors.PBFTCheckpointAcceptor;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetch;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCollectorServant;
import br.ufba.lasid.jds.util.Debugger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckpointCollectorServant extends PBFTCollectorServant<PBFTCheckpoint>{

//    Object sortedReplierID;

    PBFTCheckpointAcceptor acceptor;
    
    public PBFTCheckpointCollectorServant(){

    }

    public PBFTCheckpointCollectorServant(PBFT p){
        setProtocol(p);
        acceptor = new PBFTCheckpointAcceptor(p);
    }

    protected synchronized boolean accept(PBFTCheckpoint checkpoint){
        return acceptor.accept(checkpoint);
    }

//    /**
//     * Collect the request sent by the client.
//     * @param checkpoint -- the client request.
//     */
//    protected synchronized boolean accept(PBFTCheckpoint checkpoint){
//
//        PBFTServer pbft = (PBFTServer)getProtocol();
//
//        /**
//         * If the preprepare message wasn't sent by a group member then it will
//         * be discarded.
//         */
//        if(!pbft.wasSentByAGroupMember(checkpoint)){
//            Debugger.debug(
//              "[PBFTCheckpointCollectorServant] s"   + pbft.getLocalServerID()   +
//              ", at time " + pbft.getClock().value() + ", discarded " + checkpoint      +
//              " because it wasn't sent by a member of the group " + pbft.getLocalGroup()
//            );
//
//            return false;
//        }
//
//        /**
//         * If the preprepare message wasn't sent by a group member then it will
//         * be discarded.
//         */
//        long lowWaterMark = pbft.getCheckpointLowWaterMark();
//        long highWaterMark = pbft.getCheckpointHighWaterMark();
//        long seqn = checkpoint.getSequenceNumber();
//
//        if(lowWaterMark > seqn){
//            Debugger.debug(
//              "[PBFTCheckpointCollectorServant] s"   + pbft.getLocalServerID()     +
//              ", at time " + pbft.getClock().value() + ", discarded " + checkpoint +
//              " because it has sequence number lower than current low water mark " +
//              "(LCWM = " + lowWaterMark + "). "
//            );
//
//            return false;
//        }
//
//        pbft.updateState(checkpoint);
//
//        String entryKey = checkpoint.getSequenceNumber().toString();
//
//        Quorum q  = pbft.getStateLog().getQuorum(PBFT.CHECKPOINTQUORUMSTORE, entryKey);
//
//        if(q != null && q.complete()){
//
//            Debugger.debug(
//              "[PBFTCheckpointCollectorServant] s" + pbft.getLocalServerID()     +
//              ", at time " + pbft.getClock().value() + ", has already complete  a quorum for " +
//              " checkpoint with sequence number (" + seqn + ")."
//            );
//
//            if(seqn > highWaterMark){
//                Debugger.debug(
//                  "[PBFTCheckpointCollectorServant] s" + pbft.getLocalServerID()     +
//                  ", at time " + pbft.getClock().value() + ", has detected a stable " +
//                  " checkpoint certificate with sequence number (" + seqn + ") " +
//                  "greater than its high checkpoint water mark (HCWK = " + highWaterMark + ")."
//                );
//                Debugger.debug(
//                  "[PBFTCheckpointCollectorServant] s" + pbft.getLocalServerID()     +
//                  ", at time " + pbft.getClock().value() + ", is going to start " +
//                  " a start transfer procedure."
//                );
////                pbft.setLockCheckpoint(true);
//                emit(createFetchMessage());
//                return false;
//            }
//
//            pbft.doCheckpoint(seqn);
//
//            return true;
//        }
//
//        return false;
//
//    }
//
//    protected Object getReplierID(){
//
//        PBFTServer pbft = (PBFTServer)getProtocol();
//        /**
//         * Get the local group
//         */
//        IGroup g = pbft.getLocalGroup();
//
//        int range = g.getGroupSize();
//
//        sortedReplierID = null;
//        /**
//         * While a replier has not been selected.
//         */
//        while(sortedReplierID == null){
//
//            /**
//             * Sort a process
//             */
//            int pindex = (int) (Math.random()* range);
//
//            IProcess p = (IProcess) g.getMembers().get(pindex);
//
//            /**
//             * If the selected process isn't the primary and isn't the local replica
//             * then it'll be selected.
//             */
//            if(!pbft.isPrimary(p) && !p.getID().equals(pbft.getLocalServerID())){
//                sortedReplierID = p.getID();
//            }
//        }
//
//        return sortedReplierID;
//    }
//    protected PBFTFetch createFetchMessage(){
//
//        PBFTServer pbft = (PBFTServer)getProtocol();
//
//        PBFTFetch f = new PBFTFetch(pbft.getCheckpointLowWaterMark(), getReplierID(), pbft.getLocalServerID());
//
//        return f;
//
//    }
//
//    public synchronized void emit(PBFTServerMessage message){
//        PBFTServer pbft = (PBFTServer)getProtocol();
//
//        SignedMessage m;
//
//        try {
//
//            m = pbft.getAuthenticator().encrypt(message);
//
//            IGroup g = pbft.getLocalGroup();
//            IProcess s = pbft.getLocalProcess();
//
//            PDU pdu = new PDU();
//            pdu.setSource(s);
//            pdu.setDestination(g);
//            pdu.setPayload(m);
//
//            pbft.getCommunicator().multicast(pdu, g);
//
//            Debugger.debug(
//              "[PBFTCheckpointCollectorServant]s" +  pbft.getLocalServerID() +
//              " sent " + message + " at timestamp " + pbft.getClock().value() +
//              " to  " + g + "."
//            );
//
//
//        } catch (Exception ex) {
//            Logger.getLogger(PBFTCheckpointCollectorServant.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
//        }
//
//    }
   


    public boolean canConsume(Object object) {

        if(object instanceof PBFTCheckpoint)
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
