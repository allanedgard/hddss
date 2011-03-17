/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft.comm;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewACK;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetch;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetchMetaData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMetaData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.jbft.pbft.comm.communicators.PBFTCommunicator;
import br.ufba.lasid.jds.prototyping.hddss.Agent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class SimulatedPBFTCommunicator extends PBFTCommunicator{
    
    protected Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
    
    public SimulatedPBFTCommunicator(Agent agent) {
         setAgent(agent);
    }

    @Override
    public void multicast(IMessage m, IGroup g) {
        
        synchronized(agent.lock){
            int source = agent.ID;
            int now   = (int)agent.infra.clock.value();
            
            for(Object p : g.getMembers()){
                int dest = (Integer) ((IProcess)p).getID();
                int destin = dest;
                int type  = getMSGTYPE(m);

                agent.send(
                 new br.ufba.lasid.jds.prototyping.hddss.Message(
                    source, destin, type, 0, now, m
                 )
                );
                
            }
            
            agent.lock.notify();

        }
    }

    public void unicast(IMessage m, IProcess p) {
        synchronized(agent.lock){
            int dest = (Integer) p.getID();

            int source = agent.ID;
            int destin = dest;
            int now   = (int) agent.infra.clock.value();
            int type  = getMSGTYPE(m);

            agent.send(
             new br.ufba.lasid.jds.prototyping.hddss.Message(
                source, destin, type, 0, now, m
             )
            );
            agent.lock.notify();
        }
    }

    @Override
    public void receive(IMessage m) {
        synchronized(agent.lock){
            //Debugger.debug("[p"+agent.ID +"] received " + m + " at time " +agent.infra.clock.value());
            super.receive(m);
            agent.lock.notify();
        }
    }

    public int getMSGTYPE(IMessage m){
        if(m instanceof PDU){
            return getMSGTYPE((IMessage)((PDU)m).getPayload());
        }

        if(m instanceof SignedMessage){
            try {
                return getMSGTYPE((IMessage) ((SignedMessage)m).getSignedObject().getObject());
            } catch (Exception ex) {
                Logger.getLogger(SimulatedPBFTCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            } 
        }

        if(m instanceof PBFTRequest)        return  0;
        if(m instanceof PBFTPrePrepare)     return  1;
        if(m instanceof PBFTPrepare)        return  2;
        if(m instanceof PBFTCommit)         return  3;
        if(m instanceof PBFTCheckpoint)     return  4;
        if(m instanceof PBFTFetch)          return  5;
        if(m instanceof PBFTData)           return  6;
        if(m instanceof PBFTBag)            return  7;
        if(m instanceof PBFTStatusActive)   return  8;
        if(m instanceof PBFTReply)          return  9;
        if(m instanceof PBFTChangeView)     return 10;
        if(m instanceof PBFTChangeViewACK)  return 11;
        if(m instanceof PBFTNewView)        return 12;
        if(m instanceof PBFTFetchMetaData)  return 13;
        if(m instanceof PBFTMetaData)       return 14;


        return -1;
    }
}
