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
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMetaData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusPending;
import br.ufba.lasid.jds.jbft.pbft.comm.communicators.PBFTCommunicator;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
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
            //int now   = (int)agent.infra.clock.value();
            int now   = (int)agent.infra.cpu.value();
            
            //for(Object p : g.getMembers()){
                int dest = (Integer) g.getID();
                int destin = dest;
                int type  = getMSGTYPE(m);

                agent.send(
                 new br.ufba.lasid.jds.prototyping.hddss.Message(
                    source, destin, type, 0, now, m, true
                 )
                );
                
            //}
            
            agent.lock.notify();

        }
    }

    public void unicast(IMessage m, IProcess p) {
        synchronized(agent.lock){
            int dest = (Integer) p.getID();

            int source = agent.ID;
            int destin = dest;
            //int now   = (int) agent.infra.clock.value();
            int now   = (int) agent.infra.cpu.value();
            int type  = getMSGTYPE(m);

            setSendTime(m);
            
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
            super.receive(m);
            agent.lock.notify();
        }
    }
    public void setSendTime(IMessage m){
        if(m instanceof PDU){
            setSendTime((IMessage)((PDU)m).getPayload());
            return;
        }

        if(m instanceof SignedMessage){
            try {
                setSendTime((IMessage) ((SignedMessage)m).getSignedObject().getObject());
                return;
            } catch (Exception ex) {
                Logger.getLogger(SimulatedPBFTCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }

        if(m instanceof PBFTMessage){
           ((PBFTMessage)m).setSendTime(agent.infra.clock.value());
           //((PBFTMessage)m).setSendTime(agent.infra.cpu.value());
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

        if(m instanceof PBFTRequest)        return IPBFTServer.REQUEST;
        if(m instanceof PBFTPrePrepare)     return IPBFTServer.PREPREPARE;
        if(m instanceof PBFTPrepare)        return IPBFTServer.PREPARE;
        if(m instanceof PBFTCommit)         return IPBFTServer.COMMIT;
        if(m instanceof PBFTCheckpoint)     return IPBFTServer.CHECKPOINT;
        if(m instanceof PBFTFetch)          return IPBFTServer.FETCH;
        if(m instanceof PBFTMetaData)       return IPBFTServer.METADATA;
        if(m instanceof PBFTData)           return IPBFTServer.DATA;
        if(m instanceof PBFTBag)            return IPBFTServer.BAG;
        if(m instanceof PBFTStatusActive)   return IPBFTServer.STATUSACTIVE;
        if(m instanceof PBFTReply)          return IPBFTServer.REPLY;
        if(m instanceof PBFTChangeView)     return IPBFTServer.CHANGEVIEW;
        if(m instanceof PBFTChangeViewACK)  return IPBFTServer.CHANGEVIEWACK;
        if(m instanceof PBFTNewView)        return IPBFTServer.NEWVIEW;
        if(m instanceof PBFTStatusPending)  return IPBFTServer.STATUSPENDING;


        return -1;
    }
}
