/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.fmm;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.ISystemEntity;
import br.ufba.lasid.jds.adapters.IEventListener;
import br.ufba.lasid.jds.architectures.Architecture;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageQueue;
import br.ufba.lasid.jds.comm.communicators.ICommunicator;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.fmm.Mode;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewACK;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetch;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMetaData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.security.IMessageAuthenticator;
import br.ufba.lasid.jds.prototyping.hddss.IClock;
import br.ufba.lasid.jds.util.IScheduler;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTServerMode extends Mode implements IPBFTServer{

    public PBFTServerMode(int imode, PBFTServerMultiModeMachine machine) {
        super(imode, machine);
    }

   @Override
    public PBFTServerMultiModeMachine getMachine(){
        return (PBFTServerMultiModeMachine)this.machine;
    }

    public void emitChangeView() {
        getMachine().getProtocol().emitChangeView();
    }

    public IMessageAuthenticator getAuthenticator() {
        return getMachine().getProtocol().getAuthenticator();
    }

//    public long getCurrentCommitSEQ() {
//        return getMachine().getProtocol().getCurrentCommitSEQ();
//    }

    public long getCurrentExecuteSEQ() {
        return getMachine().getProtocol().getCurrentExecuteSEQ();
    }

    public long getCurrentPrePrepareSEQ() {
        return getMachine().getProtocol().getCurrentPrePrepareSEQ();
    }

//    public long getCurrentPrepareSEQ() {
//        return getMachine().getProtocol().getCurrentPrepareSEQ();
//    }

    public Object getLocalServerID() {
        return getMachine().getProtocol().getLocalServerID();
    }

    public long getSlidingWindowSize() {
        return getMachine().getProtocol().getSlidingWindowSize();
    }

    public Architecture getArchitecture() {
        return getMachine().getProtocol().getArchitecture();
    }

    public IClock getClock() {
        return getMachine().getProtocol().getClock();
    }

    public long getClockValue() {
        return getMachine().getProtocol().getClockValue();
    }

    public IGroup getLocalGroup() {
        return getMachine().getProtocol().getLocalGroup();
    }

    public Object getLocalProcessID() {
        return getMachine().getProtocol().getLocalProcessID();
    }

    public IScheduler getScheduler() {
        return getMachine().getProtocol().getScheduler();
    }

    public void setArchitecture(Architecture architecture) {
        getMachine().getProtocol().setArchitecture(architecture);
    }

    public void setAuthenticator(IMessageAuthenticator authenticator) {
        getMachine().getProtocol().setAuthenticator(authenticator);
    }

    public void setClock(IClock clock) {
        getMachine().getProtocol().setClock(clock);
    }

    public void setLocalGroup(IGroup g) {
        getMachine().getProtocol().setLocalGroup(g);
    }

    public void setScheduler(IScheduler scheduler) {
        getMachine().getProtocol().setScheduler(scheduler);
    }

    public void shutdown() {
        getMachine().getProtocol().shutdown();
    }

    public IProcess getLocalProcess() {
        return getMachine().getProtocol().getLocalProcess();
    }

    public ISystemEntity getRemoteProcess() {
        return getMachine().getProtocol().getRemoteProcess();
    }

    public void setLocalProcess(IProcess process) {
        getMachine().getProtocol().setLocalProcess(process);
    }

    public void setRemoteProcess(ISystemEntity process) {
        getMachine().getProtocol().setRemoteProcess(process);
    }

    public ICommunicator getCommunicator() {
        return getMachine().getProtocol().getCommunicator();
    }

    public void setCommunicator(ICommunicator comm) {
        getMachine().getProtocol().setCommunicator(comm);
    }

    Hashtable<String, MessageQueue> queuetable = new Hashtable<String, MessageQueue>();
    public MessageQueue getQueue(String name){
        MessageQueue queue = queuetable.get(name);
        if(queue == null){
            queue = new MessageQueue();
            queuetable.put(name, queue);
        }
        return queue;
    }

    public void setBatchSize(int bsize) {
        getMachine().getProtocol().setBatchSize(bsize);
    }

    public void setBatchTimeout(Long btimeout) {
        getMachine().getProtocol().setBatchTimeout(btimeout);
    }

    public void setCheckpointFactor(Long factor) {
        getMachine().getProtocol().setCheckpointFactor(factor);
    }

    public void setCheckpointPeriod(long period) {
        getMachine().getProtocol().setCheckpointPeriod(period);
    }

//    public void setCurrentPrimaryID(Object pid) {
//        getMachine().getProtocol().setCurrentPrimaryID(pid);
//    }

    public void setCurrentViewNumber(Integer viewn) {
        getMachine().getProtocol().setCurrentViewNumber(viewn);

    }

    public Integer getCurrentViewNumber(){
       return getMachine().getProtocol().getCurrentViewNumber();
    }

    public int getServiceBFTResilience(){
       return getMachine().getProtocol().getCurrentViewNumber();
    }
    public void setPrimaryFaultTimeout(Long pftimeout) {
        getMachine().getProtocol().setPrimaryFaultTimeout(pftimeout);
    }

    public void setRejuvenationWindow(long rwindow) {
        getMachine().getProtocol().setRejuvenationWindow(rwindow);
    }

    public void setSendStatusPeriod(long ssperiod) {
        getMachine().getProtocol().setSendStatusPeriod(ssperiod);
    }

    public void setSlidingWindowSize(Long swsize) {
        getMachine().getProtocol().setSlidingWindowSize(swsize);
    }

    public void emitFetch() {
        getMachine().getProtocol().emitFetch();
    }

    public IServer getServer() {
        return getMachine().getProtocol().getServer();
    }

   public String getDefaultFileName() {
      return getMachine().getProtocol().getDefaultFileName();
   }

   public void setDefaultFileName(String defaultFileName) {
      getMachine().getProtocol().getDefaultFileName();
   }



    public void loadState() {
        getMachine().getProtocol().loadState();
    }

    public void schedulePeriodicStatusSend() {
        getMachine().getProtocol().schedulePeriodicStatusSend();
    }

    public void setServer(IServer server) {
        getMachine().getProtocol().setServer(server);
    }

    public void addListener(IEventListener listener, Method m) {
        getMachine().getProtocol().addListener(listener, m);
    }

   public boolean changing() {
      return getMachine().getProtocol().changing();
   }

   public boolean overloaded() {
      return getMachine().getProtocol().overloaded();
   }

   public boolean running() {
      return getMachine().getProtocol().running();
   }

   public boolean starting() {
      return getMachine().getProtocol().starting();
   }

    protected void handle(PBFTServerMessage m){
       if(m instanceof PBFTPrePrepare){
          handle((PBFTPrePrepare)m);
       }

       if(m instanceof PBFTPrepare){
          handle((PBFTPrepare)m);
       }

       if(m instanceof PBFTCommit){
          handle((PBFTCommit)m);
       }

       if(m instanceof PBFTCheckpoint){
          handle((PBFTCheckpoint)m);
       }

       if(m instanceof PBFTFetch){
          handle((PBFTFetch)m);
       }

       if(m instanceof PBFTMetaData){
          handle((PBFTMetaData)m);
       }

       if(m instanceof PBFTData){
          handle((PBFTData)m);
       }

       if(m instanceof PBFTChangeView){
          handle((PBFTChangeView)m);
       }

       if(m instanceof PBFTChangeViewACK){
          handle((PBFTChangeViewACK)m);
       }

       if(m instanceof PBFTNewView){
          handle((PBFTNewView)m);
       }

       if(m instanceof PBFTStatusActive){
          handle((PBFTStatusActive)m);
       }

       if(m instanceof PBFTBag){
          handle((PBFTBag)m);
       }
    }

   @Override
   public void exit() {
      for(String qkey : queuetable.keySet()){
         MessageQueue oQueue = queuetable.get(qkey);
         MessageQueue iQueue = getMachine().getQueue(qkey);

         IMessage m = oQueue.remove();
         if(!iQueue.contains(m)){
            iQueue.enqueue(m);
         }

      }
   }

   @Override
   public void enter() {

      Hashtable<String, MessageQueue> oQueuetable = getMachine().getQueuetable();

      for(String qkey : oQueuetable.keySet()){

         MessageQueue oQueue = oQueuetable.get(qkey);
         MessageQueue iQueue = getQueue(qkey);

         IMessage m = oQueue.remove();
         if(!iQueue.contains(m)){
            iQueue.enqueue(m);
         }

      }
   }




}
