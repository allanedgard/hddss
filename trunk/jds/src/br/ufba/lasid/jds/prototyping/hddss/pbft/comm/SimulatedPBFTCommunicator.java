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
import br.ufba.lasid.jds.jbft.pbft.IPBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
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
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.communicators.PBFTCommunicator;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.server.PBFTServer;
import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.prototyping.hddss.Simulator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class SimulatedPBFTCommunicator extends PBFTCommunicator{
    
    protected Agent agent;
    protected IPBFT pbft;
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

   public IPBFT getPbft() {
      return pbft;
   }

   public void setPbft(IPBFT pbft) {
      this.pbft = pbft;
   }


    public SimulatedPBFTCommunicator(Agent agent, IPBFT pbft) {
         setAgent(agent);
         setPbft(pbft);
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

    int replies = 0;
    public void unicast(IMessage m, IProcess p) {
        synchronized(agent.lock){
            int dest = (Integer) p.getID();

            int source = agent.ID;
            int destin = dest;
            //int now   = (int) agent.infra.clock.value();
            int now   = (int) agent.infra.cpu.value();
            int type  = getMSGTYPE(m);

            if(type == IPBFTServer.REPLY){
               if(pbft instanceof PBFTServer){
                  PBFTServer proto = (PBFTServer)pbft;
                  StatedPBFTRequestMessage loggedRequest = proto.getRequestInfo().getStatedRequest((PBFTReply)getMSG(m));
                  long t0 = loggedRequest.getRequestReceiveTime();
                  long t1 = loggedRequest.getReplySendTime();
                  replies ++;
                  Simulator.reporter.stats("response time of replicated state machine", t1 - t0);
                  Simulator.reporter.stats("response time of server" + agent.ID , t1 - t0);
                  if(t1 > 0){
                     Simulator.reporter.assign("general mean throughput server" + agent.ID , ((double)replies)/((double)t1));
                  }
               }
            }
            
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

    public PBFTMessage getMSG(IMessage m){
        if(m instanceof PDU){
            return getMSG((IMessage)((PDU)m).getPayload());
        }

        if(m instanceof SignedMessage){
            try {
                return getMSG((IMessage) ((SignedMessage)m).getSignedObject().getObject());
            } catch (Exception ex) {
                Logger.getLogger(SimulatedPBFTCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }

        if(m instanceof PBFTMessage){
           return (PBFTMessage)m;
        }

        return null;
       
    }

    public int getMSGTYPE(IMessage m){

        PBFTMessage pm = getMSG(m);
        if(pm == null) return -1;

        if(pm instanceof PBFTRequest)        return IPBFTServer.REQUEST;
        if(pm instanceof PBFTPrePrepare)     return IPBFTServer.PREPREPARE;
        if(pm instanceof PBFTPrepare)        return IPBFTServer.PREPARE;
        if(pm instanceof PBFTCommit)         return IPBFTServer.COMMIT;
        if(pm instanceof PBFTCheckpoint)     return IPBFTServer.CHECKPOINT;
        if(pm instanceof PBFTFetch)          return IPBFTServer.FETCH;
        if(pm instanceof PBFTMetaData)       return IPBFTServer.METADATA;
        if(pm instanceof PBFTData)           return IPBFTServer.DATA;
        if(pm instanceof PBFTBag)            return IPBFTServer.BAG;
        if(pm instanceof PBFTStatusActive)   return IPBFTServer.STATUSACTIVE;
        if(pm instanceof PBFTReply)          return IPBFTServer.REPLY;
        if(pm instanceof PBFTChangeView)     return IPBFTServer.CHANGEVIEW;
        if(pm instanceof PBFTChangeViewACK)  return IPBFTServer.CHANGEVIEWACK;
        if(pm instanceof PBFTNewView)        return IPBFTServer.NEWVIEW;
        if(pm instanceof PBFTStatusPending)  return IPBFTServer.STATUSPENDING;


        return -1;
    }
}
