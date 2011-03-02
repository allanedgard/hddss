/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors;

import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCollectorServant;
import br.ufba.lasid.jds.util.Debugger;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckpointCollectorServant extends PBFTCollectorServant<PBFTCheckpoint>{


    public PBFTCheckpointCollectorServant(){

    }

    public PBFTCheckpointCollectorServant(PBFT p){
        setProtocol(p);
    }


    /**
     * Collect the request sent by the client.
     * @param checkpoint -- the client request.
     */
    protected synchronized boolean accept(PBFTCheckpoint checkpoint){

        PBFTServer pbft = (PBFTServer)getProtocol();

        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        if(!pbft.wasSentByAGroupMember(checkpoint)){
            Debugger.debug(
              "[PBFTCheckpointCollectorServant] s"   + pbft.getLocalServerID()   +
              ", at time " + pbft.getClock().value() + ", discarded " + checkpoint      +
              " because it wasn't sent by a member of the group " + pbft.getLocalGroup()
            );

            return false;
        }

        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        long lowWaterMark = pbft.getCheckpointLowWaterMark();
        long highWaterMark = pbft.getCheckpointHighWaterMark();
        long seqn = checkpoint.getSequenceNumber();
        
        if(lowWaterMark > seqn && seqn <= highWaterMark){
            Debugger.debug(
              "[PBFTCheckpointCollectorServant] s"   + pbft.getLocalServerID()     +
              ", at time " + pbft.getClock().value() + ", discarded " + checkpoint +
              " because it has a sequence number out of allowed range " +
              "(LCWM = " + lowWaterMark + " ; HCWM = " + highWaterMark + ") "
            );

            return false;
        }

        pbft.updateState(checkpoint);

        String entryKey = checkpoint.getSequenceNumber().toString();
        
        Quorum q  = pbft.getStateLog().getQuorum(PBFT.CHECKPOINTQUORUMSTORE, entryKey);

        if(q != null && q.complete()){

            Debugger.debug(
              "[PBFTCheckpointCollectorServant] s" + pbft.getLocalServerID()     +
              ", at time " + pbft.getClock().value() + ", has already complete  a quorum for " +
              " checkpoint with sequence number (" + seqn + ")."
            );

            pbft.doCheckpoint(seqn);

            return true;
        }
        
        return false;

    }



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
