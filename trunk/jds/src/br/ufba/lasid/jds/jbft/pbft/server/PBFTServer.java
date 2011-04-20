/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server;

import br.ufba.lasid.jds.BaseProcess;
import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.ISystemEntity;
import br.ufba.lasid.jds.adapters.AfterEventtable;
import br.ufba.lasid.jds.adapters.BeforeEventtable;
import br.ufba.lasid.jds.adapters.IAfterEventListener;
import br.ufba.lasid.jds.adapters.IBeforeEventListener;
import br.ufba.lasid.jds.adapters.IEventListener;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.ft.util.CheckpointLogEntry;
import br.ufba.lasid.jds.ft.IRecoverableServer;
import br.ufba.lasid.jds.ft.util.PartList;
import br.ufba.lasid.jds.ft.util.PartTree;
import br.ufba.lasid.jds.ft.util.PartTree.PartEntry;
import br.ufba.lasid.jds.ft.util.Parttable;
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
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.util.DigestList;
import br.ufba.lasid.jds.util.JDSUtility;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage.RequestState;
import br.ufba.lasid.jds.comm.MessageCollection;
import br.ufba.lasid.jds.comm.MessageQueue;
import br.ufba.lasid.jds.decision.voting.SoftQuorum;
import br.ufba.lasid.jds.group.decision.Vote;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewACKInfo;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewInfo;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpointInfo;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommitInfo;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewViewInfo;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepareInfo;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepareInfo;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequestInfo;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusPending;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.jbft.pbft.server.decision.BagSubject;
import br.ufba.lasid.jds.jbft.pbft.server.decision.CheckpointSubject;
import br.ufba.lasid.jds.jbft.pbft.server.decision.MetaDataSubject;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTNewViewConstructor;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTTimeoutDetector;
import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.state.managers.IRecovarableStateManager;
import br.ufba.lasid.jds.util.IPayload;
import br.ufba.lasid.jds.util.ISchedule;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PBFTServer extends PBFT implements IPBFTServer{

    /* state recovery manager */
    IRecovarableStateManager rStateManager;

    public void loadState(){
       try {
           
            String defaultFName = "replica" + getLocalServerID();
           /*instatiates a empty property collection*/
            Properties initOptions = new Properties();

            /*defines the persistent storage id as replicai*/
            initOptions.put( JDSUtility.PersistentStorageID,
                             defaultFName );

            initOptions.put( JDSUtility.Filename,
                             defaultFName );


            /*defines default value for the maximum cache page size as 4096*/
            initOptions.put( JDSUtility.MaximumPageSize,
                             "4096" );

            /* Uses the JDSUtility facilities to create a Recoverable State Manager */
            rStateManager = JDSUtility.create( JDSUtility.RecovarableStateManagerProvider,
                                               initOptions );

            rStateManager.setObjectStorageID(defaultFName);
            doRollback();
            
           IRecoverableServer lServer = (IRecoverableServer)getServer();

            /* Sets the current state managed by the state manager as the current server state. */
            rStateManager.setCurrentState(lServer.getCurrentState());
            
        } catch (Exception ex) {
            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }
    @Override
    public void startup() {

        loadState();
        
        super.startup();

        schedulePeriodicStatusSend();
        
        emitFetch();
    }

    public void doCheckpoint(long seqn) {

       if(getCheckpointInfo().isEmpty()){
          return;
       }

       Object lpid = getLocalServerID();

       long fcheck = getCheckpointInfo().getFirstSequenceNumber();
       long lcheck = getCheckpointInfo().getLastSequenceNumber();
       long lexsqn = getCurrentExecuteSEQ();
       long lcwm = getLCWM();

       for(long icheck = fcheck; icheck <= lcheck && icheck <= lexsqn; icheck++){               
            SoftQuorum q = (SoftQuorum)getStateLog().getQuorum(CHECKPOINTQUORUMSTORE, String.valueOf(icheck));

            if(q == null){
               continue;
            }

            CheckpointSubject cs = (CheckpointSubject)q.getCurrentDecision();

            if(cs != null && getCheckpointInfo().hasEnough(icheck)){

               CheckpointLogEntry clogEntry = rStateManager.getLogEntry(icheck);

               String cDGST = (String) cs.getInfo(CheckpointSubject.DIGEST);

               if(!(clogEntry != null && clogEntry.getDigest() != null && clogEntry.getDigest().equals(cDGST))){
                  break;
               }

               if(!clogEntry.wasProcessed()){
                  try {
                     rStateManager.setCurrentState(clogEntry.getState());
                     rStateManager.checkpoint(clogEntry.getCheckpointID());

                     rStateManager.removeLogEntry(clogEntry.getCheckpointID());
                     clogEntry.setProcessed(true);

                     lcwm = icheck;

                  } catch (Exception ex) {
                     Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                  }
               }
            }
       }

       if(lcwm >= getStateLog().getCheckpointLowWaterMark()){
            getStateLog().setCheckpointLowWaterMark(lcwm);

            JDSUtility.debug(
               "[doCheckpoint(seqn)] s" + lpid + ", at time " + getClockValue() + ", starts the garbage collection procedure with LCWM = " + lcwm + "..."
            );

            /* clean-up the request log */
            getRequestInfo().garbage(lcwm);
            getPrePrepareInfo().gc(lcwm);
            getPrePreparebackupInfo().gc(lcwm);
            getPrepareInfo().gc(lcwm);
            getCommitInfo().gc(lcwm);            
            getCheckpointInfo().removeLowerOrEqaul(lcwm);

            /* clean-up the order state log */
            getStateLog().garbage(lcwm);

            JDSUtility.debug("[doCheckpoint(seqn)] s" + lpid + " complete the garbage collection procedure for LCWM{" + lcwm + "}");

        }else{
            JDSUtility.debug("[doCheckpoint(seqn)] s" + lpid + ", at time " + getClockValue() + ", does not have cached stated matching LCWM{" + seqn + "}");
        }
    }
        
  /*########################################################################
   # 1. Methods for handling client requests.
   #########################################################################*/
    public synchronized void handle(PBFTRequest r){
       
      Object lpid = getLocalServerID();

      JDSUtility.debug("[handle(request)] s" + lpid + ", at time " + getClockValue() + ", received " + r);

      StatedPBFTRequestMessage loggedRequest = getRequestInfo().getStatedRequest(r);
      
      /* if the request has not been logged anymore and it's a old request, so it was garbage by checkpoint procedure then I must send a null reply */
      if(loggedRequest == null && getRequestInfo().isOld(r)){
         IProcess client = new BaseProcess(r.getClientID());
         PBFTReply reply = new PBFTReply(r, null, lpid, getCurrentViewNumber());
         emit(reply, client);
         return;
         
      }
      
      try{
         /*if the request is new and hasn't added yet then it'll be added */
         if(loggedRequest == null){
            /* I received a new request so a must log it */
            loggedRequest = getRequestInfo().add(getRequestDigest(r), r, RequestState.WAITING);
         }

         /* if I have a entry in request log but I don't have the request then I must update my request log. */
         if(loggedRequest.getRequest() == null) loggedRequest.setRequest(r);
         
        /*if the request was served the I'll re-send the related reply if it has been logged yet.*/
         if(loggedRequest.getState().equals(RequestState.SERVED)){
            JDSUtility.debug("[handle(request)] s" + lpid + " has already served " + r);

            /* retransmite the reply when the request was already served */
            PBFTReply reply = getRequestInfo().getReply(r);
            IProcess client = new BaseProcess(r.getClientID());
            emit(reply, client);
            return;
         }
         
         /* If I'm changing then I'll do nothing more .*/
         if(changing()) return;

         PBFTPrePrepare pp = getPrePreparebackupInfo().get(getCurrentViewNumber(), getCurrentPrimaryID(), loggedRequest.getDigest());

         if(pp != null && !isPrimary()){
            /* For each digest in backuped pre-prepare, I haven't all request then it'll be discarded. */
            DigestList digests = new DigestList();
            for(String digest : pp.getDigests()){
               if(!getRequestInfo().hasRequest(digest)){
                  digests.add(digest);
               }
            }
            
            if(digests.isEmpty()){
               handle(pp);
               getPrePreparebackupInfo().rem(pp);
               return;
            }            
         }

         boolean   committed = loggedRequest.getState().equals( RequestState.COMMITTED );
           
//         /* if my request was commit and it hasn't been served yet I must check the stated of the request */
         if(committed){
            tryExecuteRequests();
            return;
         }
         
         /* performs the batch procedure if the server is the primary replica. */
         if(isPrimary()){
            JDSUtility.debug("[handle(request)] s" + lpid + " (primary) is executing the batch procedure for " + r + ".");
            batch();
         }else{
            /* schedules a timeout for the arriving of the pre-prepare message if the server is a secundary replica. */
            scheduleViewChange();
         }//end if is primary
         
      }catch(Exception e){
         e.printStackTrace();
      }
    }//end handle(request)

    protected String getRequestDigest(PBFTRequest r) throws Exception{
         String digest = getAuthenticator().getDigest(r);
         return digest;
    }
    
   public void batch(){
      if(isPrimary()){
         if(!overloaded()){
            if(hasACompleteBatch()){
               emitBatch();
            }else{
               scheduleSendBatch();
            }//end if has a complete batch
            return;
         }//end if !overloaded
         JDSUtility.debug("[batch()]s" + getLocalServerID() + ", at time " + getClockValue() + ", has been in overloaded state.");
      }//end if is primary
   }//end batch(digest)

    protected boolean hasACompleteBatch(){
       //we must decide if we are going to use the number or size of requests in queue.
       return getBatchSize() <= getRequestInfo().getWaitingQueueSize();//getRequestInfo().getSizeInBytes();
    }//is a complete batch

   protected void emitBatch(){
      synchronized(this){

         revokeSendBatch();

         int viewn = getCurrentViewNumber();
         long seqn = getStateLog().getNextPrePrepareSEQ();

         PBFTRequestInfo rinfo = getRequestInfo();
         if(!rinfo.hasSomeWaiting()){
            return;
         }
         /* creates a new pre-prepare message */
         PBFTPrePrepare pp = null;

         int size = 0;

         /* while has not achieved the batch size and there is digests in queue */
         String digest = null;
         while(size < getBatchSize() && (digest = rinfo.getFirtRequestDigestWaiting())!= null){
            if(pp == null){
               pp = new PBFTPrePrepare(viewn, seqn, getLocalServerID());
            }
            pp.getDigests().add(digest);
            rinfo.assign(digest, RequestState.PREPREPARED);
            size += 1;//rinfo.getRequestSize(digest);
         }

         if(pp == null){
            return;
         }
         /* emits pre-prepare */
         emit(pp, getLocalGroup().minus(getLocalProcess()));

         /* update log current pre-prepare */
         handle(pp);

         /* if there is digest then it will schedule a send batch */
         if(rinfo.hasSomeWaiting()){
            batch();
         }//end if digest queue is no empty

      }//end synchronized(this)
   }//end emitBatch()

    public void revokeSendBatch(){
        getBatchTimer().cancel();
    }

    /* batch timer */
    protected ISchedule btimer = null;

   protected ISchedule getBatchTimer(){
      if(btimer == null){
         PBFTTimeoutDetector ttask = new PBFTTimeoutDetector() {
            @Override
            public void onTimeout() {
               JDSUtility.debug("[onTimeout()] s" + getLocalServerID() + " had a batch timer expired at time " + getClockValue());
               emitBatch();
            }
         };
         btimer = getScheduler().newSchedule();
         btimer.setTask(ttask);
         
      }//end if btimer is null

      return btimer;
   }
    
    public void scheduleSendBatch(/*String digest*/){

        long now = getClockValue();
        
        if(!getBatchTimer().workingAt(now)){
            long timeout = getBatchingTimeout();
            long timestamp = now + timeout;

            getBatchTimer().schedule(timestamp);
            JDSUtility.debug("[doSchedule(digest)] s" + getLocalServerID() + " scheduled a batch timeout for " + getBatchTimer().getTimestamp());

        }//end batch time is no working
    }//end scheduleSendBatch()

    /* change-view timer */
    protected ISchedule vtimer = null;
    
    protected ISchedule getViewTimer(){
        if(vtimer == null){
             PBFTTimeoutDetector ttask = new PBFTTimeoutDetector() {
                @Override
                public void onTimeout() {
                   if(getRequestInfo().hasSomeWaiting()){
                     emitChangeView();
                   }
                }
            };
            vtimer = getScheduler().newSchedule();
            vtimer.setTask(ttask);
            ttask.put("TIMEOUT", getPrimaryFaultTimeout());
        }//end if change-view timer is null
        return vtimer;        
    }//end getViewTimer()

   /**
    * Schedule a view change in case of late response for the primary.
    * @param request - the client request.
    * @param timeout - the view change timeout.
    */
   public void scheduleViewChange(){
      PBFTRequestInfo rinfo = getRequestInfo();

      if(!isPrimary() && rinfo.hasSomeWaiting()){
         
         if(!overloaded() || changing()){
            long now = getClockValue();

            if(!getViewTimer().workingAt(now)){

               long timeout = getPrimaryFaultTimeout();
               PBFTTimeoutDetector ttask = (PBFTTimeoutDetector) getViewTimer().getTask();

               if(ttask != null && ttask.get("TIMEOUT") != null){
                  timeout = (Long)ttask.get("TIMEOUT");
                  JDSUtility.debug("[scheduleViewChange()] s" + getLocalServerID() + " computed changeview timeout equal to " + timeout);
               }

               long timestamp = now + timeout;
               getViewTimer().schedule(timestamp);
               return;
            }
            return;
         }//end change-view timer is working
        JDSUtility.debug("[batch()]s" + getLocalServerID() + ", at time " + getClockValue() + ", has been in overloaded state.");
      }//end if it is not primary
      
   }//scheduleViewChange()

    /**
     * revoke the timer assigned to a client request (i.e. the change view timer).
     * @param leafPartDigest
     */

    public void restartViewChangeTimer(){
         revokeViewChange();
         scheduleViewChange();
    }
    public void revokeViewChange(){
       if(!isPrimary()){
         getViewTimer().cancel();
       }
    }//end revokeViewChange()

 /*########################################################################
  # 2. Methods for handling pre-prepare messages.
  #########################################################################*/
   public void handle(PBFTPrePrepare pp){
      
      Object lpid = getLocalServerID();
      long    now = getClockValue();

      JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", received " + pp);

      /* If I received a invalid preprepare then I'll discard it. */
      if(!(pp != null && pp.getSequenceNumber() != null && pp.getViewNumber() != null && pp.getDigests() != null && pp.getReplicaID() != null)){
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", discarded " + pp + " because it's a malformed pre-prepare.");
         return;
      }
      
      /* If the received pre-prepare wasn't sent by a group member then I'll discard it. */
      if(!wasSentByAGroupMember(pp)){
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", discarded " + pp + " because it wasn't sent by group member.");
         return;
      }

      long lcwm = getLCWM();
      long hcwm = getHCWM();
      long seqn = pp.getSequenceNumber();

      /*If seqn(pp) not in (lcwm, hcwm] then pp will be discarded. */
      if(!(lcwm < seqn && seqn <= hcwm)){         
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", discarded " + pp + " because its SEQN{" + seqn +"} not in (" + lcwm + "; " + hcwm + "].");
         return;
      }

      int itView = pp.getViewNumber();
      int myView = getCurrentViewNumber();
      
      /*If a previous pre-prepare with same sequence number was accepted it'll be discarded. */
      if(getPrePrepareInfo().count(itView, seqn) > 0){
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", discarded " + pp + " because it has a previous pre-prepare with same SEQN{" + seqn + "}");
         return;
      }

      /* If the pre-prepare wasn't sent in my current view then it won't be processed. */
      if(itView > myView){
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", cann't process " + pp + " because it doesn't belong to the CURR-VIEW{" + myView + "}");
         return;
      }

      /* if the pre-prepare wasn't sent by the primary of this or previous view then it'll be discarded. */
      Object rpid = pp.getReplicaID();
      if(!isPrimary(rpid, itView)){
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", cann't process " + pp + " because it wasn't sent by the primary.");
         return;
      }


      /* For each digest in pre-prepare, I haven't all request then it'll be discarded. */
      DigestList digests = new DigestList();      
      for(String digest : pp.getDigests()){
         if(!getRequestInfo().hasRequest(digest)){
            digests.add(digest);
         }
      }

      if(!digests.isEmpty()){
         getPrePreparebackupInfo().put(pp);
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", cann't process " + pp + " because it has missed requests (" + digests +").");
         return;
      }

      /*I store pre-prepares from current or previous views. */
      getPrePrepareInfo().put(pp);

      if(itView != myView){
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", cann't prepare " + pp + " because it doesn't belongs to VIEW(" + myView + ").");
         return;
      }

      getStateLog().updateNextPrePrepareSEQ(pp);

      for(String digest : pp.getDigests()){
         getRequestInfo().assign(digest, RequestState.PREPREPARED);
         getRequestInfo().assign(digest, seqn);
      }
      
      /* Everything is fine, so I can send a prepare if I'm not the primary of the current view. */
      if(!isPrimary()){
         restartViewChangeTimer();
         emitPrepare(pp);
      }

   }//end handle(pp)
   
   public void emitPrepare(PBFTPrePrepare pp){

      PBFTPrepare p = createPrepareMessage(pp);

      emit(p, getLocalGroup().minus(getLocalProcess()));

      handle(p);
      
   }

    /**
     * Create a new Prepare Message from a pre-prepare message.
     * @param pp - the pre-prepare message.
     * @return the created prepare message.
     */
    public PBFTPrepare createPrepareMessage(PBFTPrePrepare pp){
        PBFTPrepare p = new PBFTPrepare(pp, getLocalServerID());
        return p;
    }

 /*########################################################################
  # 3. Methods for handling prepare messages.
  #########################################################################*/
   public void handle(PBFTPrepare p){

      Object lpid = getLocalServerID();
      long    now = getClockValue();

      JDSUtility.debug("[handle(prepare)] s" + lpid + ", at time " + now + ", received " + p);

      /* If I received a invalid prepare then I'll discard it. */
      if(!(p != null && p.getSequenceNumber() != null && p.getViewNumber() != null && p.getDigests() != null && p.getReplicaID() != null)){
         JDSUtility.debug("[handle(prepare)] s" + lpid + ", at time " + now + ", discarded " + p + " because it's a malformed prepare.");
         return;
      }

      /* If the received prepare wasn't sent by a group member then I'll discard it. */
      if(!wasSentByAGroupMember(p)){
         JDSUtility.debug("[handle(prepare)] s" + lpid + ", at time " + now + ", discarded " + p + " because it wasn't sent by group member.");
         return;
      }

      long lcwm = getLCWM();
      long hcwm = getHCWM();
      long seqn = p.getSequenceNumber();

      /*If seqn(pp) not in (lcwm, hcwm] then p will be discarded. */
      if(!(lcwm < seqn && seqn <= hcwm)){
         JDSUtility.debug("[handle(prepare)] s" + lpid + ", at time " + now + ", discarded " + p + " because its SEQN{" + seqn +"} not in (" + lcwm + "; " + hcwm + "].");
         return;
      }

      int itView = p.getViewNumber();
      int myView = getCurrentViewNumber();
      
      /* If the preprepare wasn't sent by the current primary replica then it'll be discarded. */
      Object currentPrimaryID = getCurrentPrimaryID();
      if(wasSentByPrimary(p, itView)){
         JDSUtility.debug("[handle(prepare)] s" + lpid + ", at time " + now + ", discarded " + p + " because it was sent by the primary server s" + currentPrimaryID);
         return;
      }

      /* If the prepare was sent in my current view then I'll check and process it.*/
      if(itView > myView){
         JDSUtility.debug("[handle(prepare)] s" + lpid + ", at time " + now + ", discarded " + p + " because it wasn't sent in CURR-VIEW{" + myView + "}");
         return;
      }
      
      if(!getPrepareInfo().put(p)){
         JDSUtility.debug("[handle(prepare)] s" + lpid + ", at time " + now + ", discarded " + p + " because it is a duplicaded prepare SEQN{" + seqn + "}");
         return;
      }

      if(itView != myView){
         JDSUtility.debug("[handle(prepare)] s" + lpid + ", at time " + now + ", cann't proceed because " + p + " in CURR-VIEW{" + myView + "}");
         return;         
      }

      int f = getServiceBFTResilience();

      if(getPrepareInfo().count(p) < (2 * f)){
         return;
      }

      /*if it wasn't pre-prepared and I have a quorum then probably a missed pre-prepare or request messages. */
      PBFTPrePrepare pp = getPrePrepareInfo().get(myView, seqn);
      
      if(pp == null){
         pp = new PBFTPrePrepare(myView, seqn, getCurrentPrimaryID());
         pp.getDigests().addAll(p.getDigests());
         getPrePrepareInfo().put(pp);
         getPrePreparebackupInfo().rem(pp);
      }

      if(!isPrimary() && getPrepareInfo().get(myView, seqn, lpid) == null){
         getStateLog().updateNextPrePrepareSEQ(pp);
         restartViewChangeTimer();
         emitPrepare(pp);
      }

      if(getCommitInfo().get(myView, seqn, lpid) == null){
         for(String digest : pp.getDigests()){
            if(!getRequestInfo().hasRequest(digest)){
               getRequestInfo().add(digest, null, RequestState.PREPARED);
            }else{
               getRequestInfo().assign(digest, RequestState.PREPARED);
            }
            getRequestInfo().assign(digest, seqn);
         }

         emitCommit(myView, seqn);

      }
         
   }


   public void emitCommit(int viewn, long seqn){
      PBFTCommit commit = createCommitMessage(viewn, seqn);
      emit(commit, getLocalGroup().minus(getLocalProcess()));
      handle(commit);
   }
   
    public PBFTCommit createCommitMessage(int viewn, long seqn){
       return new PBFTCommit(viewn, seqn, getLocalServerID());
    }
    
    public PBFTCommit createCommitMessage(PBFTPrepare p){
        PBFTCommit c = new PBFTCommit(p, getLocalServerID());
        return c;
    }

/*########################################################################
  # 4. Methods for handling commit messages.
  #########################################################################*/
    public void handle(PBFTCommit c){
      Object lpid = getLocalServerID();
      long    now = getClockValue();

      JDSUtility.debug("[handle(commit)] s" + lpid + ", at time " + now + ", received " + c);

      /* If I received a invalid prepare then I'll discard it. */
      if(!(c != null && c.getSequenceNumber() != null && c.getViewNumber() != null && c.getReplicaID() != null)){
         JDSUtility.debug("[handle(commit)] s" + lpid + ", at time " + now + ", discarded " + c + " because it's a malformed commit.");
         return;
      }

      /* If the received commit wasn't sent by a group member then I'll discard it. */
      if(!wasSentByAGroupMember(c)){
         JDSUtility.debug("[handle(commit)] s" + lpid + ", at time " + now + ", discarded " + c + " because it wasn't sent by group member.");
         return;
      }

      long lcwm = getLCWM();
      long hcwm = getHCWM();
      long seqn = c.getSequenceNumber();

      /*If seqn(c) not in (lcwm, hcwm] then c will be discarded. */
      if(!(lcwm < seqn && seqn <= hcwm)){
         JDSUtility.debug("[handle(commit)] s" + lpid + ", at time " + now + ", discarded " + c + " because its SEQN{" + seqn +"} not in (" + lcwm + "; " + hcwm + "].");
         return;
      }

      int itView = c.getViewNumber();
      int myView = getCurrentViewNumber();

      /* If the prepare was sent in my current view then I'll check and process it.*/
      if(itView > myView){
         JDSUtility.debug("[handle(commit)] s" + lpid + ", at time " + now + ", discarded " + c + " because it wasn't sent in CURR-VIEW{" + myView + "}");
         return;
      }
      
      if(!getCommitInfo().put(c)){
         JDSUtility.debug("[handle(commit)] s" + lpid + ", at time " + now + ", discarded " + c + " because it is a duplicaded commit SEQN{" + seqn + "}");
         return;
      }

      if(itView != myView){
         JDSUtility.debug("[handle(commit)] s" + lpid + ", at time " + now + ", cann't proceed because " + c + " in CURR-VIEW{" + myView + "}");
         return;
      }

      int f = getServiceBFTResilience();

      if(getCommitInfo().count(myView, seqn) < (2 * f + 1)){
         return;
      }
        
      PBFTPrepare p = getPrepareInfo().get(myView, seqn, lpid);

      if(p != null && getCommitInfo().get(myView, seqn, lpid) == null){
         getRequestInfo().assign(seqn, RequestState.COMMITTED);
         emitCommit(myView, seqn);
         return;
      }

      tryExecuteRequests();
      
    }


 /*########################################################################
  # 5. Methods for handling checkpoint messages.
  #########################################################################*/
    public void handle(PBFTCheckpoint checkpoint){

        JDSUtility.debug("[PBFTServer:handle(checkpoint)] s" + getLocalServerID() + ", at time " + getClockValue() + ", received " + checkpoint);

        if(isValid(checkpoint)){
           
           long seqn = checkpoint.getSequenceNumber();
           long lcwm = getLCWM();
           long hcwm = getHCWM();
           long  now = getClockValue();

           getCheckpointInfo().put(checkpoint);
           
           Object lpid = getLocalProcessID();
           
           CheckpointSubject cs = getDecision(checkpoint);
           
           if(getCheckpointInfo().hasEnough(seqn) && cs != null && lcwm < seqn && seqn <= hcwm){

              if(seqn > hcwm){

                 JDSUtility.debug("[handle(checkpoint)] s"+ lpid +", at time "+ now +", detected a stable checkpoint certificate with SEQN{"+ seqn +"} > HCWM{"+ hcwm +"}.");
                 JDSUtility.debug("[handle(checkpoint)] s"+ lpid +", at time "+ now +", is going to start a start transfer procedure.");

                 emitFetch();
                 return;

              }//end if I've a unsynchronized state

              if(getCheckpointInfo().getMine(lpid, seqn) != null){                                  
                 doCheckpoint(seqn);                 
              }
              
           }//end if has a decision
           
        }//end if isValid(checkpoint)
        
    }//end handle(checkpoint);
    
    public boolean isValid(PBFTCheckpoint c){
        /* If the prepare hasn't a valid sequence / view number then it'll force a change view. */
        if(!(c != null && c.getSequenceNumber() != null && c.getDigest() != null)){
           return false;
        }

     Object lpid = getLocalServerID();
     long   seqn = c.getSequenceNumber();
     long   lcwm = getLCWM();
     long    now = getClockValue();
     int   gsize = getLocalGroup().getGroupSize();

      if(lcwm > seqn){
         
          JDSUtility.debug("[isValid(checkpoint)] s" + lpid + ", at  time " + now + ", discarded " + c + " because it has a SEQN{checkpoint} < LCWM{" + lcwm + "}). ");

          return false;
      }//end if lcwm  > seqn


        /* If the checkpoint message wasn't sent by a group member then it will be discarded. */
        if(!wasSentByAGroupMember(c)){

            JDSUtility.debug("[isValid(checkpoint)] s" + lpid + ", at time " + now + ", discarded " + c + " because it wasn't sent by a group member.");

            return false;
        }

        /*If a previous checkpoint with same sequence number was accepted */
        if(getCheckpointInfo().count(seqn) >= gsize){

            JDSUtility.debug(
              "[isValid(checkpoint)] s" + lpid + ", at time " + now + ", discarded " + c + " because there is so much checkpoints " +
              "(SIZE(CHECKPOINTSET{" + seqn + "}) >= " + gsize + ")."
            );

           return false;
        }

        if(getCheckpointInfo().contains(c)){

            JDSUtility.debug("[isValid(checkpoint)] s" + lpid + ", at time " + now + ", discarded " + c + " because it's duplicated.");

            return false;

        }

        return true;

    }//end isValid(checkpoint)

    /**
     * Update the state of the PBFT. Insert the pre-prepare message in the log entry.
     * @param m
     */
   public CheckpointSubject getDecision(PBFTCheckpoint c){
      if(!(c != null && c.getSequenceNumber() != null && c.getDigest() != null)){
         return null;
      }

      Object lpid = getLocalServerID();

      long   seqn = c.getSequenceNumber();
      long    now = getClockValue();
      String qkey = String.valueOf(seqn);

      SoftQuorum q = (SoftQuorum)getStateLog().getQuorum(CHECKPOINTQUORUMSTORE, qkey);

      if(q == null){
         int f = getServiceBFTResilience();

         q = new SoftQuorum(2 * f + 1);

         getStateLog().getQuorumTable(CHECKPOINTQUORUMSTORE).put(qkey, q);
         
      }

      q.add(new Vote(c.getReplicaID(), new CheckpointSubject(c)));
      
      JDSUtility.debug("[getDecision(checkpoint)] s"  + lpid + ", at time " + now + ", updated a entry in its log for " + c);

      CheckpointSubject cs = (CheckpointSubject) q.decide();

      if(cs != null){         
         JDSUtility.debug("[getDecision(checkpoint)] s" + lpid + ", at time " + now + ", completed quorum for checkpoint (" + seqn + ").");
      }

      return cs;
      
   }

    protected Object getReplierID(){
         /* Get the local group */
         IGroup g = getLocalGroup();

         int range = g.getGroupSize();

         Object sortedReplierID = null;
         /* While a replier has not been selected.*/
         while(sortedReplierID == null){

             /* Sort a process */
             int pindex = (int) (Math.random()* range);

             IProcess p = (IProcess) g.getMembers().get(pindex);

             /* If the selected process isn't the primary and isn't the local replica then it'll be selected.*/
             if(!isPrimary(p) && !p.getID().equals(getLocalServerID())){
                 sortedReplierID = p.getID();
             }
         }
         return sortedReplierID;
    }



 /*########################################################################
  # 6. Methods for handling state transferring messages (a) metadata fetch.
  #########################################################################*/
    
    public void handle(PBFTMetaData receivedMD) {
        if(isValid(receivedMD)){
            MetaDataSubject mds = getDecision(receivedMD);
            if(mds != null){
                /* update the partition table with all received and certified subpartitions. */
                PartList subparts = (PartList) mds.getInfo(MetaDataSubject.SUBPARTS);
                
                updateParttable(subparts);
                
                try{
                    doStepNextStateTransfer(receivedMD.getReplicaID(), null);
                }catch(Exception except){
                    except.printStackTrace();
                }
                
            }//end received meta-data is certificated
        }//end received meta-data is validated
    }//end tryExecuteRequests received meta-data

    Parttable transferring = new Parttable();
    Parttable  transferred = new Parttable();

    public void doStepNextStateTransfer(Object replierID, Long lrectransfer) throws Exception{
        int LEVELS = rStateManager.getParttreeLevels();
        int ORDER  = rStateManager.getParttreeOrder();

        /* if is a valid recod id*/
        if(lrectransfer != null && lrectransfer >= 0){
            /* move part from in transferring state to transferred state */
            PartEntry transferredPart = transferring.remove(lrectransfer);
            if(transferredPart != null){
                transferred.put(lrectransfer, transferredPart);
            }
        }

        /* getChangeView the part with the maximum record index (this is, in depthest level)*/
        PartEntry part = getPartWithMaximumPartindex(transferring);

        /* if still exists a part */
        while(part != null){
            long lpart = part.getPartLevel();
            long ipart = part.getPartIndex();
            long cpart = part.getPartCheckpoint();

            /* if the part is a leaf the fetch the data */
            if(PartTree.isPage(LEVELS, lpart)){
                //Object replierID = receivedMD.getReplicaID();
                emitFetch(lpart, ipart, cpart, replierID);
                return;
            }else{/*else check the part*/
                try{
                    String dpart = PartTree.subpartsDigest(transferred, part, ORDER, LEVELS);
                    /* if the part is valid then it'll be moved from transferring to transferred state */
                    if(part.getDigest().equals(dpart)){
                        lrectransfer = PartTree.getRecordindex(ORDER, lpart, ipart);
                        if(lrectransfer != null && lrectransfer >= 0){
                            transferring.remove(lrectransfer);
                            transferred.put(lrectransfer, part);
                        }
                    }else{
                        /* else this replica will select another reply and retrive the part and its subparts */
                        emitFetch(lpart, ipart, cpart, getReplierID());
                        return;
                    }
                    
                }catch(Exception except){
                    except.printStackTrace();
                }
            }
            /*getChangeView next*/
            part = getPartWithMaximumPartindex(transferring);
        }

        /* insert the transferred parts into the parttree */
        ArrayList<Long> recids = new ArrayList(transferred.keySet());
        for(Long recid : recids){
            part = transferred.get(recid);
            rStateManager.put(recid, part);
            transferred.remove(recid);
        }

        doRollback();
    }

    /* the state transfer doesn't work properly and it must be reviewed. */
    
    public void doRollback() throws Exception{
        /* rollback to last stable checkpoint */
        rStateManager.rollback();

        /* gets the last stable checkpoint, or zero if it doesn't exist */
        long seqn = rStateManager.getCurrentCheckpointID();

        IRecoverableServer lServer = (IRecoverableServer)getServer();
            
         getStateLog().setNextExecuteSEQ(seqn + 1);
         getStateLog().setCheckpointLowWaterMark(seqn);

         if(seqn >= 0){
            lServer.setCurrentState(rStateManager.getCurrentState());
         }else{
            lServer.setCurrentState(null);
         }

         if(seqn > getCurrentPrePrepareSEQ()){
            getStateLog().setNextPrePrepareSEQ(seqn+1);
         }else{
            tryExecuteRequests();
         }
        
    }
    
    public void updateParttable(PartList parts){

        int order  = rStateManager.getParttreeOrder();
        for(int p = 0; p < parts.size(); p++){

            PartEntry part = parts.get(p);
            long ipart = part.getPartIndex();
            long lpart = part.getPartLevel();
            long recid = PartTree.getRecordindex(order, lpart, ipart);
            transferring.put(recid, part);
            
        }//end for each part in parts
        
    }//end updateParttable(parts)

    public PartEntry getPartWithMaximumPartindex(Parttable parttable) throws Exception{

        if(!parttable.isEmpty()){
            ArrayList<Long> irecords = new ArrayList<Long>();
            irecords.addAll(parttable.keySet());

            Collections.sort(irecords);

            int last = irecords.size() - 1;

            return parttable.get(irecords.get(last));
        }

        return null;
    }
    
    public PBFTMetaData createMetaDataMessage(long lpart, long ipart, long checkpointID, PartList subparts){
        PBFTMetaData md = new PBFTMetaData(checkpointID, lpart, ipart, getLocalServerID());
        md.setSubparts(subparts);
        return md;
    }
    
    public void handle(PBFTFetch f){
        
        JDSUtility.debug("[PBFTServer:handle(fetch)] s" + getLocalServerID() + ", at time " + getClockValue() + ", received " + f);
        
        if(isValid(f)){
            long rlcSEQ = f.getLastStableCheckpointSequenceNumber();
            long cpart = f.getPartCheckpoint();

            long lpart = f.getPartLevel();
            long ipart = f.getPartitionIndex();
            int LEVELS = rStateManager.getParttreeLevels();
            
            Object rid = f.getReplicaID();
            try {
                
                PartList subparts = null;
                PartEntry part = rStateManager.getPart(lpart, ipart);
                BaseProcess rServer = new BaseProcess(rid);
                /* store the paramenters of the partition */
                if(part != null && part.getPartLevel() == lpart && part.getPartIndex() == ipart){

                    cpart = part.getPartCheckpoint();

                    if(PartTree.isPage(LEVELS, part)){
                        /**
                         * the recovarable state manager has been prepared to work with persisitent data storage (for example see
                         * <B>getDataStorage</B>), however the pbft code hasn't been prepared yet. This implementantion of the pbft
                         * only works with volatile data storage in object storage. (Alirio Sá)
                         */
                        IMemory mem = rStateManager.getObjecStorage();
                        long ipage = part.getPartIndex();
                        IPage page = mem.readPage(part.getPartIndex());

                        emit(createDataMessage(ipage, page), rServer);

                        mem.release();
                                                
                    }else{
                        
                        /* Gets all subparts of the current part */
                        subparts = rStateManager.getFamily(lpart, ipart, rlcSEQ);

                        /* if there is parts that match with the specified select criteria */
                        if(subparts != null && !subparts.isEmpty()){
                            PBFTMetaData md = createMetaDataMessage(lpart, ipart, cpart, subparts);
                            emit(md, rServer);
                        }//end if exist subparts
                    }//end if is leaf part
                }//end if part was found
            } catch (Exception ex) {
                Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
    }
    
    public boolean isValid(PBFTFetch f){
        if(!wasSentByAGroupMember(f)){
            JDSUtility.debug(
              "[PBFTServer:isValid(fetch)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + f + " " +
              "because it wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }

        long wcwm = f.getPartCheckpoint();
        long lcwm = getStateLog().getCheckpointLowWaterMark();

        Object replier = f.getSelectedReplierID();

        if(lcwm >= wcwm){
            if(replier != null && getLocalServerID().equals(replier)){
                return true;
            }

            if(replier == null){
                return true;
            }
        }
        return false;
    }

    public void emitFetch(long lpart, long ipart, long cpart, Object replierID){
        emit(
            createFetchMessage(lpart, ipart, cpart, replierID),
            new BaseProcess(replierID)
        );
    }
    
    public void emitFetch(){
        emit(
            createFetchMessage(),
            getLocalGroup().minus(new BaseProcess(getLocalServerID()))
        );
    }

    /* Use it when we don't know the part that must be fetched and we don't know the replier */
    public PBFTFetch createFetchMessage(){
        return createFetchMessage(null);
    }

    /* Use it when we don't know the part that must be fetched but we know the replier */
    public PBFTFetch createFetchMessage(Object replierID){
        long lpart = 0, ipart = 0, cpart = -1;
        return createFetchMessage(lpart, ipart, cpart, replierID);
    }

    /*Use it when we know the exatly part that must be fetched but we know the replier */
    public PBFTFetch createFetchMessage(long lpart, long ipart, long cpart, Object replierID){
        long lcwm = getLCWM();
        PBFTFetch f = new PBFTFetch(lpart, ipart, cpart, lcwm, replierID, getLocalServerID());

        return f;

    }

    public void handle(PBFTData d){
        
        JDSUtility.debug("[PBFTServer:handle(data)] s" + getLocalServerID() + ", at time " + getClockValue() + ", " + "received " + d + ".");

        if(isValid(d)){
                        
            try{
                int  ORDER = rStateManager.getParttreeOrder();
                int LEVELS = rStateManager.getParttreeLevels();

                IPage page = d.getPage();
                long ipage = d.getPageIndex();
                long ipart = ipage;

                long recid = PartTree.getRecordindex(ORDER, LEVELS, ipart);

                PartEntry part = transferring.get(recid);

                long lpart = part.getPartLevel();
                long cpart = part.getPartCheckpoint();

                String cdigest = PartTree.leafPartDigest(ipart, cpart , page);

                /* if the partDigest of the partition match with the data partDigest, the it will processing the recovery of the page. */
                if(part.getDigest().equals(cdigest)){

                    IMemory mem = rStateManager.getObjecStorage();                                        
                    mem.writePage(page);
                    mem.release();
                    doStepNextStateTransfer(d.getReplicaID(), recid);
                }else{ 
                    /* else it will select another replier and fetch the missed page data. */
                    emitFetch(lpart, ipart, cpart, getReplierID());
                }
                                             
            }catch(Exception except){
                except.printStackTrace();
            }            
        }
    }

    public boolean isValid(PBFTData d){
        int  ORDER = rStateManager.getParttreeOrder();
        int LEVELS = rStateManager.getParttreeLevels();
        
        long ipage = d.getPageIndex();
        long recid = PartTree.getRecordindex(ORDER, LEVELS, ipage);

        if(!transferring.containsKey(recid)){
            JDSUtility.debug(
              "[PBFTServer:isValid(data)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + d +
              " because it there isn't in temporary part table."
            );

            return false;
        }

        return true;
    }


    public boolean isValid(PBFTMetaData md){
        if(!wasSentByAGroupMember(md)){
            JDSUtility.debug(
              "[PBFTServer:isValid(metadata)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + md +
              " because it wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }
        
        long lcwm = getLCWM();
        long rcmd = md.getCheckpoint();

        if(rcmd <  lcwm){
            JDSUtility.debug(
              "[PBFTServer:isValid(metadata)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + md + " because it has " +
              "a LCWM (" + rcmd + ")" + "< s" + getLocalServerID() +":LCWM(" + lcwm + ")."
            );

            return false;
        }

        return true;

    }

   public MetaDataSubject getDecision(PBFTMetaData md){
      
      boolean completed = false;
      Object lServerID = getLocalServerID();

      /* If the preprepare is null then do nothing. */
      if(md == null) return null;

      /* Get composite key of the prepare. */

      Long entryKey = md.getSequenceNumber();

      /* If the entry key is diferent of null then update state. Otherwise do nothing. */
      if(entryKey != null) {

         SoftQuorum q = (SoftQuorum)getStateLog().getQuorum(METADATAQUORUMSTORE, entryKey.toString());

         if(q == null){
            int f = getServiceBFTResilience();

            q = new SoftQuorum(2 * f + 1);

            getStateLog().getQuorumTable(METADATAQUORUMSTORE).put(entryKey.toString(), q);
         }

         /*TODO: evaluate if "q.getCurrentDecision()" is a better implementation */
         MetaDataSubject decision = (MetaDataSubject)q.decide();

         if(decision != null){
            completed = true;
         }

         q.add(new Vote(md.getReplicaID(), new MetaDataSubject(md)));

         JDSUtility.debug("[PBFTServer:getDecision(metadata)] s" + lServerID + ", at time " + getClockValue() + ", updated a entry in its log for " + md);


         if(decision == null){
            decision = (MetaDataSubject)q.decide();
         }

         if(!completed && decision != null){
            long cseqn = (Long) decision.getInfo(MetaDataSubject.CHECKPOINT);
            JDSUtility.debug("[PBFTServer:getDecision(metadata)] s" + lServerID + ", at time " + getClockValue() + ", complete a quorum for metada with LCWM (" + cseqn + ").");
            return decision;
         }
      }
      return null;
   }

    public PBFTData createDataMessage(long ipage, IPage page){
        return new PBFTData(ipage, page, getLocalServerID());
    }

   /*########################################################################
     # 7. Methods for handling bag of messages.
     #########################################################################*/

    public void handle(PBFTBag bag){

        JDSUtility.debug("[PBFTServer:handle(bag)] s" + getLocalServerID() + ", at time " + getClockValue() + ", received " + bag);

        if(isValid(bag)){
           int f = getServiceBFTResilience();
           JDSUtility.debug("[PBFTServer:handle(bag)] s" + getLocalServerID() + ", at time " + getClockValue() + ", is going to processing " + bag);
           for(IMessage m: bag.getMessages()){

              if(m instanceof PBFTRequest){

                 PBFTRequest r = (PBFTRequest) m;
                 
                 if(!getRequestInfo().hasRequest(r)){
                    handle(r);
                 }

              }

              if(m instanceof PBFTPrePrepare){
                 PBFTPrePrepare pp = (PBFTPrePrepare) m;
                 if(!getPrePrepareInfo().contains(pp)) {
                    handle(pp);
                 }
              }

              if(m instanceof PBFTPrepare){
                 PBFTPrepare p = (PBFTPrepare) m;
                 if(!getPrepareInfo().contains(p)){
                   handle(p);
                 }
              }

              if(m instanceof PBFTCommit){
                 PBFTCommit c = (PBFTCommit) m;
                 if(!getCommitInfo().contains(c)){
                   handle(c);
                 }
              }
              
              if(m instanceof PBFTCheckpoint){
                  handle((PBFTCheckpoint)m);
              }

              if(m instanceof PBFTNewView){
                  handle((PBFTNewView)m);
              }

              if(m instanceof PBFTChangeView){
                  handle((PBFTChangeView)m);
              }

              if(m instanceof PBFTChangeViewACK){
                  handle((PBFTChangeViewACK)m);
              }
           }
        }
    }

    public boolean isValid(PBFTBag bag){
        /* If the preprepare message wasn't sent by a group member then it will be discarded. */
        if(!wasSentByAGroupMember(bag)){
            JDSUtility.debug(
              "[PBFTServer:isValid(bag)] s"   + getLocalServerID() +  ", at time " + getClockValue() + ", discarded " + bag +" because it " +
              "wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }

        return true;
    }

    /**
     * Update the state of the PBFT. Insert the pre-prepare message in the log entry.
     * @param m
     */
   public BagSubject getDecision(PBFTBag bag){
      /* If the preprepare is null then do nothing.*/
      boolean completed = false;
      if(bag == null) return null;

      /* Get composite key of the prepare. */
      Long entryKey = bag.getSequenceNumber();
      Object lServerID = getLocalServerID();

      /* If the entry key is not null then it'll update state. */
      if(entryKey != null) {
         SoftQuorum q = (SoftQuorum)getStateLog().getQuorum(BAGQUORUMSTORE, entryKey.toString());
         if(q == null){
            int f = getServiceBFTResilience();
            q = new SoftQuorum(2 * f + 1);
            getStateLog().getQuorumTable(BAGQUORUMSTORE).put(entryKey.toString(), q);
         }

         /*TODO: evaluate if "q.getCurrentDecision()" is a better implementation */
         BagSubject decision = (BagSubject) q.decide();

         if(decision != null){
            completed = true;
         }

         q.add(new Vote(bag.getReplicaID(), new BagSubject(bag)));

         if(decision == null){
            decision = (BagSubject) q.decide();
         }

         JDSUtility.debug("[PBFTServer:getDecision(bag)] s"  + lServerID +", at time " + getClockValue() + ", updated a entry in its log for " + bag);

         if(!completed && decision != null){
            long seqn = (Long) decision.getInfo(BagSubject.SEQUENCENUMBER);
            JDSUtility.debug("[PBFTServer:getDecision(bag] s" + lServerID + ", at time " + getClockValue() + ", completed  quorum for bag with EXEC-SEQ (" + seqn + ")");
            return decision;
         }
      }
      return null;
   }

   /*########################################################################
     # 8. Methods for handling status-active messages.
     #########################################################################*/

    public void handle(PBFTStatusActive sa){

      Object lpid = getLocalProcessID();

      JDSUtility.debug("[handle(statusactive)] s" + lpid + ", at time " + getClockValue() + ", received " + sa);

      if(!(
              sa != null && sa.getViewNumber() != null && sa.getPrepared() != null && sa.getCommited() != null &&
              sa.getLastExecutedSEQ() != null && sa.getLastStableCheckpointSEQ() != null && sa.getReplicaID() != null 
      )){
         JDSUtility.debug("[handle(statusactive)] s" + lpid + ", at time " + getClockValue() + ", discarded " + sa + " because it's a malformed message.");
         return;
      }

      if(!wasSentByAGroupMember(sa)){
         JDSUtility.debug("[handle(statusactive)] s" + lpid + ", at time " + getClockValue() + ", discarded " + sa + " because it wasn't sent by a group member.");
         return;
      }

      if(sa.getReplicaID().equals(lpid)){
         JDSUtility.debug("[handle(statusactive)] s" + lpid + ", at time " + getClockValue() + ", discarded " + sa + " because it was sent by the local server.");
         return;
      }
      
      long rlesq = sa.getLastExecutedSEQ();
      long rlcwm = sa.getLastStableCheckpointSEQ();
      int view = sa.getViewNumber();

      BitSet prepared = sa.getPrepared();
      BitSet commited = sa.getCommited();

      Long lpseq = getPrepareInfo().getLastSequenceNumber(view);
      Long lcseq = getCommitInfo().getLastSequenceNumber(view);

      if(lpseq == null) lpseq = getCurrentExecuteSEQ();
      if(lcseq == null) lcseq = getCurrentExecuteSEQ();
      
      long llcwm = getLCWM();

       
      Object lsid = getLocalServerID();

      PBFTBag bag = new PBFTBag(lsid);

      bag.setSequenceNumber(sa.getSequenceNumber());

       
      if(sa instanceof PBFTStatusPending){
         PBFTStatusPending sp = (PBFTStatusPending) sa;
         int viewn = sp.getViewNumber();

         for(int i = 0;  i < getLocalGroup().getGroupSize(); i++){
            IProcess p = getLocalGroup().getMember(i);
            boolean found = false;
            for(Object pid : sp.getChangeViewReplicas()){
               if(p.getID().equals(pid)){
                  found = true;
                  break;
               }
            }

            if(!found){
               PBFTChangeView cv = getChangeViewInfo().getChangeView(viewn, p.getID());
               if(cv != null){
                  bag.addMessage(cv);
                  PBFTChangeViewACK ack = getChangeViewInfo().getMyACK(view, p.getID());
                  if(ack != null){
                     bag.addMessage(ack);
                  }
               }
            }
         }

         boolean hasNewView = sp.hasNewView();

         if(!hasNewView){
            PBFTNewView nv = getNewViewInfo().get(viewn);
            if(nv != null){
               bag.addMessage(nv);
            }
         }
       }

       for(String digest : sa.getDigests()){
          if(getRequestInfo().hasRequest(digest)){
             bag.addMessage(getRequestInfo().getRequest(digest));
          }
       }
       
       for(long csqn = rlesq + 1; lpseq >= 0 && csqn <= lpseq ; csqn ++){

         int i = (int)(csqn - (rlesq+1));

         if(csqn > rlcwm){
            if(isPrimary()){
               if(!prepared.get(i)){

                  PBFTPrePrepare pp = (PBFTPrePrepare) getPrePrepareInfo().get(view, csqn);

                  if(pp != null) {
                     bag.addMessage(pp);
                  }

               }
            }

            PBFTPrepare p = null;

            if(!prepared.get(i) && (p = getPrepareInfo().get(view, csqn, lsid)) != null) {
               bag.addMessage(p);
            }
         }
       }
      
       for(long csqn = rlesq + 1; lcseq >= 0 && csqn <= lcseq; csqn ++){

         int i = (int)(csqn - (rlesq+1));

         if(csqn > rlcwm){
            PBFTCommit  c = null;

            if(!commited.get(i) && (c = (PBFTCommit ) getCommitInfo().get(view, csqn, lsid))!= null) {
               bag.addMessage(c);
            }
         }
       }

      if(rlcwm < llcwm){
          try {
              
              CheckpointLogEntry clogEntry = rStateManager.getBiggestLogEntry();
              if(clogEntry != null){
                  long seqn = clogEntry.getCheckpointID();
                  String digest = clogEntry.getDigest();
                  PBFTCheckpoint checkpoint = new PBFTCheckpoint(seqn, digest, getLocalServerID());
                  bag.addMessage(checkpoint);
              }

          } catch (Exception ex) {
              Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
              ex.printStackTrace();
          }
      }

      if(!bag.isEmpty()){
          emit(bag, new BaseProcess(sa.getReplicaID()));
      }
    }

    PBFTTimeoutDetector periodStatusTimer = null;
    ISchedule stimer;

    protected ISchedule getStatusTimer(){
        if(stimer == null){
            PBFTTimeoutDetector ttask = new PBFTTimeoutDetector() {
                    @Override
                    public void onTimeout() {
                        emitStatus(getLocalGroup().minus(getLocalProcess()));
                        schedulePeriodicStatusSend();

                    }
            };

            stimer = getScheduler().newSchedule();
            stimer.setTask(ttask);
        }

        return stimer;
    }

    public void emitStatus(IGroup g){
       if(!changing()){
         emit(createStatusActiveMessage(), g);
         return;
       }

       emit(createStatusPendingMessage(), g);
       
    }
   public void schedulePeriodicStatusSend() {
        long now = getClockValue();
        long period = getSendStatusPeriod();
        
        getStatusTimer().schedule(now + period);
        
   }

    public PBFTStatusActive createStatusActiveMessage(){
        int viewn = getCurrentViewNumber();
        long lesq = getCurrentExecuteSEQ();
        long lcwm = getLCWM();
        long hcwm = getHCWM();
        Long lpsq = getPrepareInfo().getLastSequenceNumber(viewn);
        Long lcsq = getCommitInfo().getLastSequenceNumber(viewn);

        if(lpsq == null) lpsq = getCurrentExecuteSEQ();
        if(lcsq == null) lcsq = getCurrentExecuteSEQ();

        Object lsid = getLocalProcessID();

        PBFTStatusActive sa = new PBFTStatusActive(lesq, lsid, viewn, lesq, lcwm);

        for(long csqn = lesq + 1; lpsq >= 0 && csqn <= lpsq && csqn > lcwm && csqn <= hcwm ; csqn++){

           int i = (int)(csqn - (lesq + 1));

           SoftQuorum q = (SoftQuorum) getStateLog().getPrepareQuorum(csqn);

           boolean prepared = (q != null && q.getCurrentDecision() != null);
           
           sa.getPrepared().set(i, prepared);

        }
        
        for(long csqn = lesq + 1; lcsq >= 0 && csqn <= lcsq && csqn > lcwm && csqn <= hcwm; csqn++){
            int i = (int)(csqn - (lesq + 1));

            SoftQuorum q = (SoftQuorum) getStateLog().getCommitQuorum(csqn);
            
            boolean commited = (q != null && q.getCurrentDecision() != null);

            sa.getCommited().set(i, commited);

        }

        for(long csqn = lcwm + 1; csqn <= lesq; csqn ++){
           DigestList digests = getRequestInfo().getDigestsOfMissedRequests(csqn);
           sa.getDigests().addAll(digests);
        }
        
        return sa;

    }

    public PBFTStatusPending createStatusPendingMessage(){

       int viewn = getCurrentViewNumber();

       PBFTNewView nv = getNewViewInfo().get(viewn);

       long lseq = getCurrentExecuteSEQ();
       long lcwm = getLCWM();

       
       PBFTStatusPending sp = new PBFTStatusPending(lseq, viewn, lseq, getLCWM(), getLocalServerID(), nv != null);

       int gs = getLocalGroup().getGroupSize();

       for(int i = 0; i < gs; i++){
          IProcess p = getLocalGroup().getMember(i);

          PBFTChangeView cv = getChangeViewInfo().getChangeView(viewn, p.getID());

          if(cv != null){
             sp.getChangeViewReplicas().add(p.getID());
          }          
       }
       
        for(long csqn = lcwm + 1; csqn <= lseq; csqn ++){
           DigestList digests = getRequestInfo().getDigestsOfMissedRequests(csqn);
           sp.getDigests().addAll(digests);
        }

       return sp;
       
    }

   /*########################################################################
     # 9. Execute sequence number.
     #########################################################################*/

    public synchronized void tryExecuteRequests(){
        Object lpid = getLocalProcessID();

        JDSUtility.debug("[PBFTSever:handle(token)] s" + lpid + ",  at time " + getClockValue() + ", is going to execute requests.");

        //if(isValid(proctoken)){
         long startSEQ = getStateLog().getNextExecuteSEQ();
         long finalSEQ = getHCWM();//proctoken.getSequenceNumber();
         long lcwm = getLCWM();

         PBFTRequestInfo rinfo = getRequestInfo();

         int viewn = getCurrentViewNumber();

         int f = getServiceBFTResilience();

         for(long currSEQ = startSEQ; currSEQ <= finalSEQ && currSEQ > lcwm; currSEQ ++){

             if(!(getPrepareInfo().count(viewn, currSEQ) >= (2* f) && getCommitInfo().count(viewn, currSEQ) >= (2 * f +1))){
                return;
             }

             if(rinfo.hasSomeRequestMissed(currSEQ)){
                 JDSUtility.debug("[tryExecuteRequests()] s"  + lpid+ ", at time " + getClockValue() + ", couldn't executed " + currSEQ + " because it has a missed request.");
                 return;
             }

             if(rinfo.wasServed(currSEQ)){
                continue;
             }

             IRecoverableServer lServer = (IRecoverableServer)getServer();

             PBFTPrePrepare preprepare = getPrePrepareInfo().get(viewn, currSEQ);

             for(String digest : preprepare.getDigests()){

                 PBFTRequest request = rinfo.getRequest(digest); //statedReq.getRequest();

                 IPayload result = lServer.executeCommand(request.getPayload());

                 PBFTReply reply = createReplyMessage(request, result);

                 rinfo.assign(digest, RequestState.SERVED);
                 rinfo.assign(digest, reply);

                 JDSUtility.debug(
                     "[tryExecuteRequests()] s"  + lpid + ", at time " + getClockValue() + ", executed " + request + " (CURR-VIEWN{ " + viewn + "}; SEQN{" + currSEQ + "})."
                 );

                 JDSUtility.debug("[tryExecuteRequests()] s"  + lpid + ", at time " + getClockValue() + ", has the following state " + lServer.getCurrentState());

                 if(rinfo.isNewest(request)){
                    IProcess client = new BaseProcess(reply.getClientID());
                    emit(reply, client);
                 }

             }//end for each leafPartDigest (tryExecuteRequests and reply)

             JDSUtility.debug(
               "[tryExecuteRequests()] s"  + lpid + ", at time " + getClockValue() + ", after execute SEQN{" + currSEQ + "} has the following " +
               "state " + lServer.getCurrentState()
             );

             getStateLog().updateNextExecuteSEQ(currSEQ);

             long execSEQ = getStateLog().getNextExecuteSEQ() -1;
             long chkPeriod = getCheckpointPeriod();


             if(execSEQ > 0 && (((execSEQ+1) % chkPeriod) == 0)){
                 PBFTCheckpoint checkpoint;
                 try {
                     checkpoint = createCheckpointMessage(execSEQ);
                     getDecision(checkpoint);
                     rStateManager.addLogEntry(
                        new CheckpointLogEntry(
                                 checkpoint.getSequenceNumber(),
                                 rStateManager.byteArray(),
                                 checkpoint.getDigest()
                            )
                      );


                     emit(checkpoint, getLocalGroup().minus(getLocalProcess()));
                     handle(checkpoint);
                 } catch (Exception ex) {
                     Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }

            if(rinfo.hasSomeWaiting()){
               if(isPrimary()){
                  batch();
               }else{
                  scheduleViewChange();
               }
            }
         }
    }

    public  PBFTReply createReplyMessage(PBFTRequest r, IPayload result){

        return createReplyMessage(r, result, getCurrentViewNumber());

    }

    public PBFTReply createReplyMessage(PBFTRequest r, IPayload result, Integer viewNumber){
         PBFTReply reply = new PBFTReply(r, result, getLocalServerID(), viewNumber);
         return reply;
    }

    public void emitCheckpoint(long seqn) throws Exception{
       PBFTCheckpoint checkpoint = createCheckpointMessage(seqn);
       emit(checkpoint, getLocalGroup().minus(getLocalProcess()));
       handle(checkpoint);

    }

    public PBFTCheckpoint createCheckpointMessage(long seqn) throws Exception{

         byte[] state = rStateManager.byteArray();

         String digest = getAuthenticator().getDigest(state); //computeStateDigest(state, seqn);

         PBFTCheckpoint c = new PBFTCheckpoint(seqn, digest, getLocalServerID());

         return c;
    }

    /**
     * Send a message to a remote object (a process or a group). All emitted message are signed using the defined authenticator.
     * After the protocol signing a message it creates a PDU a use it to carry out the signed message.
     * @param msg -- the message that has to be sent.
     * @param remote -- Can be a instance of a process or a instance of a group.
     */
    public void emit(IMessage msg, ISystemEntity remote){
        SignedMessage m;

        try {

            m = getAuthenticator().encrypt(msg);

            PDU pdu = new PDU();

            pdu.setDestination(remote);
            pdu.setSource(getLocalProcess());
            pdu.setPayload(m);

            String sent = "sent";
            if(remote instanceof IProcess)
                getCommunicator().unicast(pdu, (IProcess)remote);
            else if(remote instanceof IGroup){
                getCommunicator().multicast(pdu, (IGroup)remote);
                sent = "multicast";
            }

            JDSUtility.debug("[emit(msg, dest)]s" + getLocalServerID() + " " + sent + " " + msg + ", at time " + getClockValue() + ", to " + remote + ".");

        } catch (Exception ex) {
            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    public PBFTReply createNullReplyMessage(PBFTRequest request){        
        /* It must be corrected. */
        return  new PBFTReply(request, null, getLocalServerID(), getCurrentViewNumber());
    }

  /*########################################################################
   # 11. Methods for handling change-view procedure.
   #########################################################################*/
    ArrayList<Integer> views = new ArrayList<Integer>();
    boolean uncertanty = false;
    boolean changing = false;
    protected int mode;

    public boolean running(){
        long swsize = getSlidingWindowSize();
        long currPP = getCurrentPrePrepareSEQ();
        long currEX = getCurrentExecuteSEQ();
        return (currEX + swsize >= currPP) && !changing();
    }

    public boolean overloaded(){
        long swsize = getSlidingWindowSize();
        long currPP = getCurrentPrePrepareSEQ();
        long currEX = getCurrentExecuteSEQ();
        return (currEX + swsize < currPP) && !changing();
    }

    public boolean changing(){
       return (getCurrentViewNumber() != null && getNextViewNumber() <= getCurrentViewNumber().intValue());
    }

    public boolean starting(){
       Object primaryID = getCurrentPrimaryID();

       return primaryID == null;
       
    }
    
    MessageCollection preprepareset = new MessageCollection();
    MessageCollection prepareset = new MessageCollection();
    
    /* (a.1) a change-view message is emitted because of a suspect of failure of the primary replica.                   */
    /* (a.2) change-view-ack are sent for each change-view message that was received and match with the new view number */
    public void emitChangeView() {
       
       int    view = getCurrentViewNumber();
       Object lpid = getLocalServerID();
       
        JDSUtility.debug("[emitChangeView()] s" + lpid + ", at time " + getClockValue() + ", is going to emit a change view message for (v + 1 = "  +  (view + 1) + ")");

        /* the replica makes sure that no timer is currently working. If the view change was trigged by a suspect of failure of the primary then probably it's  
           been already true. */
        revokeSendBatch();
        revokeViewChange();

         /* an exponetial timeout has to be considered to guarantee liveness when the end-to-end delay is too long (it's prevent uncessary view-changes)*/
         PBFTTimeoutDetector ttask = (PBFTTimeoutDetector) getViewTimer().getTask();
         long timeout = (Long) ttask.get("TIMEOUT");
         ttask.put("TIMEOUT", 2 * timeout);

        /* the replica moves to the next view. After that, this replica isn't accepting any message from view v < v+1. */
        setCurrentViewNumber(view +1);
        
        /* clean-up the sets P (prepare set) and Q (pre-prepare set)*/
        preprepareset.clear();
        prepareset.clear();

        long lcwm = getLCWM();
        long hcwm = getHCWM();

        /* compute Q (pre-prepare set) and P (prepare set) */
        for(long seqn = lcwm + 1; seqn <=hcwm; seqn++){
           /*If I have a prepare then its digests will be added to pre-prepared and prepared sets */
           PBFTPrepare pr = getPrepareInfo().get(lpid, seqn);
           if(pr != null){
              PBFTPrePrepare pp = new PBFTPrePrepare(view, pr.getSequenceNumber(), null);
              pp.getDigests().addAll(pr.getDigests());
              preprepareset.add(pp);
              
              prepareset.add(new PBFTPrepare(pp, null));

              for(String digest: pp.getDigests()){
                 getRequestInfo().assign(digest, RequestState.WAITING);
                 getRequestInfo().assign(digest, (Long)null);
              }

              continue;
           }

           /*If I have a pre-prepare then its digests will be added to pre-prepared set*/
           PBFTPrePrepare ppr = getPrePrepareInfo().get(seqn);
           if(ppr != null){
              PBFTPrePrepare pp = new PBFTPrePrepare(view, ppr.getSequenceNumber(), null);
              pp.getDigests().addAll(ppr.getDigests());
              preprepareset.add(pp);
              
              for(String digest: pp.getDigests()){
                 getRequestInfo().assign(digest, RequestState.WAITING);
                 getRequestInfo().assign(digest, (Long)null);
              }
           }           
        }

        
        try{
            doRollback();
        }catch(Exception e){
           e.printStackTrace();
        }

        /* update the controls for sequence number execution */
        getStateLog().setNextPrePrepareSEQ(lcwm + 1);
        getStateLog().setNextExecuteSEQ   (lcwm + 1);

        PBFTChangeView cv = createChangeViewMessage();
        
        try {
            /* gets the root of the checkpoint partition tree */
            PartEntry centry = rStateManager.getPart(0, 0);

            /* adds the pair (last stable sequence number and state digest) to compose checkpoint set C */
            if(centry != null){
               cv.addCheckpoint(centry.getPartCheckpoint(), centry.getDigest());
            }else{
               cv.addCheckpoint(lcwm, "");
            }
        } catch (Exception ex) {
            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
            cv.getCheckpointSet().clear();
            cv.addCheckpoint(lcwm, "");
        }

        /* add the pre-prepare set to current change-view message*/
        cv.getPrePrepareSet().clear();
        cv.getPrePrepareSet().addAll(preprepareset);

        /* add the prepare set to current change-view message*/
        cv.getPrepareSet().clear();
        cv.getPrepareSet().addAll(prepareset);

        /*emit the change view message to group of replicas */
        emit(cv, getLocalGroup().minus(getLocalProcess()));

        getChangeViewInfo().setMinimumViewNumber(view);
        getChangeViewInfo().put(cv);

        if(isPrimary()){
           getNewViewConstructor().addChangeView(cv);
        }else{
           finalizeNewViewConstructor();
        }

        getChangeViewInfo().gc(view);
        getChangeViewACKInfo().gc(view);
        getNewViewConstructor().gc(view);
        getNewViewInfo().gc(view);
        getPrePrepareInfo().gc(view);
        getPrepareInfo().gc(view);
        getPrePreparebackupInfo().gc(view);
        getCommitInfo().gc(view);


        emitChangeViewACK();


        uncertanty = true;        
    }

    PBFTChangeViewInfo cvi = null;

    public PBFTChangeViewInfo getChangeViewInfo(){
       if(cvi == null){
          cvi = new PBFTChangeViewInfo(getLocalServerID());
       }

       return cvi;
    }

    PBFTChangeViewACKInfo cvacki = null;

    public PBFTChangeViewACKInfo getChangeViewACKInfo(){
       if(cvacki == null){
          cvacki = new PBFTChangeViewACKInfo();
       }

       return cvacki;
    }


    public void emitChangeViewACK(){
       int   viewn = getCurrentViewNumber();

       Object    localServerID = getLocalServerID();
       IProcess currentPrimary = getPrimary(viewn);
       
       try{
         List<PBFTChangeView> cvs = getChangeViewInfo().getPendingACK(viewn);

         for(PBFTChangeView cv : cvs){

            String digest = getAuthenticator().getDigest(cv);
            PBFTChangeViewACK ack = getChangeViewInfo().buildMyACK(cv, digest);

            if(ack != null){
               getChangeViewACKInfo().put(ack);
               if(!isPrimary() && !cv.getReplicaID().equals(localServerID)){
                  emit(ack, currentPrimary);
               }

               if(isPrimary() && localServerID.equals(ack.getPrompterID())){
                  getNewViewConstructor().addChangeViewACK(ack);
               }

            }
            
         }
       }catch(Exception e){
          e.printStackTrace();
       }       
    }
    
   public void handle(PBFTChangeView cv) {
      if(!wasSentByAGroupMember(cv)){
         return;
      }

      if(getChangeViewInfo().put(cv)){
         
         int receivedView = cv.getViewNumber();
         int currentView  = getCurrentViewNumber();

         Object rpid = cv.getReplicaID();

         /* if the change-view message was sent by the current primary and such message has a view number greater than 
            the current view number then the replica will move to rview */
         if(isPrimary(rpid, currentView) && receivedView > currentView){
            setCurrentViewNumber(receivedView -  1);
            emitChangeView();
            return;
         }

         int f = getServiceBFTResilience();

         int certifiedView = getChangeViewInfo().biggestWithAtLeast(f + 1);
         
         /* if it was able to found the major view number in the set of view numbers which have at least f + 1 view-changes and that
            is greater than current view, then it will change for this */
         if(certifiedView > 0 && certifiedView > currentView){
            setCurrentViewNumber(certifiedView - 1);
            emitChangeView();
            return;            
         }


         if(!isPrimary()){

            certifiedView = getChangeViewInfo().biggestWithAtLeast(2 * f + 1);
            
            /* I have at least 2 * f + 1 view changes and I haven't received a new-view message yet, so I must restart the change view timeout
               using a exponential step approach */

            long now = getClockValue();
            
            if(certifiedView == currentView && !getViewTimer().workingAt(now)){

               scheduleViewChange();

               uncertanty = false;
               
            }
            
         }

         emitChangeViewACK();

      }else{
         System.out.println(getLocalServerID() + "  ==>  " + cv);
      }//end if is valid change
   }//end tryExecuteRequests(changeview)
        
    
    PBFTNewViewConstructor nvbuilder = null;

    public PBFTNewViewConstructor getNewViewConstructor(){
       if(nvbuilder == null){
          nvbuilder = new PBFTNewViewConstructor(this, getCheckpointFactor(), getCheckpointPeriod(), getServiceBFTResilience());
       }

       return nvbuilder;
    }

    public void finalizeNewViewConstructor(){
       nvbuilder = null;
    }
    
    public void handle(PBFTChangeViewACK ack) {

      if(!wasSentByAGroupMember(ack)){
         return;
      }
      
      if(!getChangeViewACKInfo().put(ack)){
         return;
      }

      int f = getServiceBFTResilience();
      int currentView = getCurrentViewNumber();
      int ackView = ack.getViewNumber();

      if(currentView != ackView){
         return;
      }

      PBFTChangeViewACK myack = getChangeViewInfo().getMyACK(ack.getViewNumber(), ack.getPrompterID());
      if(myack == null){
         return;
      }
      if(getChangeViewACKInfo().count(ack.getPrompterID(), myack.getDigest(), ack.getViewNumber()) < (2 * f - 1)){
         return;
      }

      if(!(isPrimary() && changing())){
         return;
      }

      PBFTChangeView cv = getChangeViewInfo().getChangeView(myack);

      if(!getNewViewConstructor().addChangeView(cv)){
         return;
      }

      getNewViewConstructor().addChangeViewACK(myack);

      getNewViewConstructor().setCheckpointQuorumSize(f + 1);

      Long lcwm = getNewViewConstructor().computeLCWM(currentView);

      if(lcwm == null){
         return;
      }

      int n = getLocalGroup().getGroupSize();

      if(!getNewViewConstructor().hasAtLeastMessages(currentView, n - f)){
         return;
      }

      PBFTNewView nv = getNewViewConstructor().buildNewView(currentView);

      if(nv == null){
         return;
      }

      nv.setReplicaID(getLocalServerID());
      emit(nv, getLocalGroup().minus(getLocalProcess()));
      handle(nv);
      
    }


    protected PBFTNewViewInfo nvi;
    public PBFTNewViewInfo getNewViewInfo(){
       if(nvi == null){
          nvi = new PBFTNewViewInfo();
       }

       return nvi;
    }
    public void handle(PBFTNewView nv) {
        JDSUtility.debug("[PBFTServer:handle(newview)] s" + getLocalServerID() + ", at time " + getClockValue() + ", received " + nv);

        if(!wasSentByAGroupMember(nv)){
           return;
        }
        
        if(!getNewViewInfo().put(nv)){
           return;
        }
        int itView = nv.getViewNumber();
        int myView = getCurrentViewNumber();

        if(itView != myView){
           return;
        }

        Object rpid = nv.getReplicaID();

        if(!isPrimary(rpid)){
           return;
        }

        for(Object prompterID : nv.getChangeViewtable().keySet()){
           /*TODO: I must check it*/
//           PBFTChangeViewACK myack = getChangeViewInfo().getMyACK(itView, prompterID);
//           String digest = nv.getChangeViewtable().get(prompterID);
//           if(!myack.getDigest().equals(digest)){
//              return;
//           }
        }

        long itLCWM = nv.getSequenceNumber();
        long myLCWM = getLCWM();

        if(itLCWM > myLCWM){
           emitFetch();
        }

        for(IMessage im : nv.getPrePrepareSet()){
           PBFTPrePrepare pp = (PBFTPrePrepare) im;
           for(String digest : pp.getDigests()){
              if(!getRequestInfo().hasRequest(digest)){
//                 return;
              }
           }

           handle(pp);
        }

        setNextViewNumber(myView + 1);
        
        if(isPrimary()){
           emitBatch();
        }else{
           restartViewChangeTimer();
        }
        
        
    }

    public PBFTChangeView createChangeViewMessage(){
        PBFTChangeView cv = new PBFTChangeView(getLCWM(), getCurrentViewNumber(), getLocalServerID() );
        return cv;
    }


  /*########################################################################
   # 11. Utility methods.
   #########################################################################*/

    /* checkpoint period mensuared in round of execution */
    protected long checkpointPeriod  = 256;

    public long getCheckpointPeriod(){
        return checkpointPeriod;
    }
    
    public  void setCheckpointPeriod(long newPeriod) {
        this.checkpointPeriod = newPeriod;
    }

    /* send-status period mensuared in milliseconds */
    protected long sendStatusPeriod = 500L;

    public long getSendStatusPeriod(){
        return sendStatusPeriod;
    }

    public void setSendStatusPeriod(long newPeriod){
        this.sendStatusPeriod = newPeriod;
    }

    /* batch timeout mensuared in milliseconds */
    protected long batchTimeout = 0;

    public Long getBatchingTimeout(){
        return batchTimeout;
    }

    public void setBatchTimeout(Long timeout){
        this.batchTimeout = timeout;
    }
    /* batch size mensuared in KB */
    protected int  batchSize = 4;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int newSize) {
        this.batchSize = newSize;
    }

    /* current view number */
    protected Integer currentViewNumber = 0;

    public Integer getCurrentViewNumber() {
        return currentViewNumber;
    }

    public void setCurrentViewNumber(Integer newViewNumber){
        this.currentViewNumber = newViewNumber;
    }

    public IProcess getPrimary(int viewn){
       
       int index = viewn % getLocalGroup().getGroupSize();
       IProcess p = getLocalGroup().getMember(index);
       if(p !=null){
          return p;
       }

       return null;

    }
    public Object getPrimaryID(int viewn){

       IProcess p = getPrimary(viewn);

       if(p !=null){
          return p.getID();
       }

       return null;
       
    }
    public Object getCurrentPrimaryID(){

       int currView = 0;

       if(getCurrentViewNumber() != null){
          currView = getCurrentViewNumber();
       }

       return getPrimaryID(currView);
       
    }

    /* current sequence number (review it late) */
    protected static long SEQ = -1;

    public synchronized static long newSequenceNumber(){
        return ++SEQ;
    }

    public static long getCurrentSequenceNumber(){
        return SEQ;
    }

    public synchronized static void updateCurrentSequenceNumber(long newSEQValue){
        SEQ = newSEQValue;
    }
    /* the application server */
    protected  IServer server;

    public void setServer(IServer newServer){
        this.server = newServer;
    }

    public IServer getServer(){
        return this.server;
    }

    protected  long slidindWindowSize = 1;

    public void setSlidingWindowSize(Long newSize) {
        slidindWindowSize = newSize;
    }

    public  long getSlidingWindowSize(){
        return slidindWindowSize;
    }

    protected PBFTRequestInfo ri = new PBFTRequestInfo();

    public PBFTRequestInfo getRequestInfo(){
       return ri;
    }

    protected PBFTPrePrepareInfo ppi = new PBFTPrePrepareInfo();
    protected PBFTPrePrepareInfo ppi_backup = new PBFTPrePrepareInfo();

    public PBFTPrePrepareInfo getPrePrepareInfo(){
       return ppi;
    }

    private PBFTPrePrepareInfo getPrePreparebackupInfo(){
       return ppi_backup;
    }

    protected PBFTPrepareInfo pri = null;

    public PBFTPrepareInfo getPrepareInfo(){
       if(pri == null){
          int f = getServiceBFTResilience();
          pri = new PBFTPrepareInfo();
       }
       return pri;
    }

    protected PBFTCommitInfo cmi = null;

    public PBFTCommitInfo getCommitInfo(){
       if(cmi == null){
          int f = getServiceBFTResilience();
          cmi = new PBFTCommitInfo();
       }
       return cmi;
    }

    protected PBFTCheckpointInfo cki = null;

    public PBFTCheckpointInfo getCheckpointInfo(){
       if(cki == null){
          int f = getServiceBFTResilience();
          cki = new PBFTCheckpointInfo( f);
       }
       return cki;
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
    
    protected  Long primaryFaultTimeout = null;

    public Long getPrimaryFaultTimeout(){return primaryFaultTimeout;}

    public void setPrimaryFaultTimeout(Long timeout){primaryFaultTimeout = timeout;}

    public boolean isLocalServer(IProcess process){
        return process != null && process.getID().equals(getLocalServerID());
    }
    public boolean isPrimary(){return isPrimary(getLocalProcess());}

    public boolean isPrimary(IProcess p){return isPrimary((Object)p.getID());}

    public boolean isPrimary(Object serverID){
       return getCurrentPrimaryID() != null && getCurrentPrimaryID().equals(serverID);
    }

    public boolean isPrimary(int view){
       int index = view % getLocalGroup().getGroupSize();
       IProcess p = getLocalGroup().getMember(index);
       return isLocalServer(p);
    }

    public boolean isPrimary(Object pid, int view){
       int index = view % getLocalGroup().getGroupSize();
       IProcess p = getLocalGroup().getMember(index);
       return p.getID().equals(pid);
    }

    public Object getLocalServerID(){return getLocalProcessID();}

    protected  long rejuvenationWindow;

    public void setRejuvenationWindow(long timeout) {this.rejuvenationWindow  = timeout;}

    public long getRejuvenationWindow() {return this.rejuvenationWindow;}

    /**
     * Checks if a message has a valid sequence number, this is: the sequence number doesn't has holes and is in a valid range.
     * @param m -- the message.
     * @return -- true if the message has a valid sequence number.
     */
    public boolean checkSequenceNumber(PBFTServerMessage m) {

        return isOrdered(m) && inAValidSequenceRange(m);

    }

    /**
     * Check if a message insert a hole in the sequence numbers.
     * @param m -- the message.
     * @return -- true if the message inserts a hole in the sequence numbers.
     */
    protected boolean isOrdered(PBFTServerMessage m){

        long nextPrePrepareSEQ = getStateLog().getNextPrePrepareSEQ();

        if(m != null && m.getSequenceNumber() != null){

            long seqn = m.getSequenceNumber();

            if(m instanceof PBFTPrePrepare){
                return seqn == nextPrePrepareSEQ;
            }
        }

        return false;

    }

    /**
     * Check if the view number of the message is equal to the current view number.
     * @param m -- the message.
     * @return -- true if the message belongs to the current view.
     */
    public boolean checkViewNumber(PBFTServerMessage m) {
        final int EQUALS = 0;

        return checkViewNumber(m, EQUALS, false);

    }
    public boolean checkViewNumber(PBFTServerMessage m, int cmp) {
       return checkViewNumber(m, cmp, false);
   }

    public boolean checkViewNumber(PBFTServerMessage m, int cmp, boolean not) {

        Integer view = m.getViewNumber();

        boolean ok =  view != null && view.compareTo(getCurrentViewNumber()) == cmp;
        
        return (not ^ ok);

    }


    /**
     * Check if a message has a sequence number between the low and high water marks defined by the checkpoint.
     * @param m -- the message.
     * @return -- true if the sequence number of the message is in the valid
     * range.
     */
    public boolean inAValidSequenceRange(PBFTServerMessage m){

        long seqn = m.getSequenceNumber();
        long low  = getLCWM();
        long high = getHCWM();

        return seqn > low && seqn <= high;

    }

    public Long getLCWM(){return getStateLog().getCheckpointLowWaterMark();}

     Long checkpointFactor;

    public void setCheckpointFactor(Long factor){checkpointFactor = factor;}
    public long getCheckpointFactor(){return checkpointFactor;}

    public Long getHCWM(){
        return getStateLog().getCheckpointHighWaterMark(getCheckpointPeriod(), getCheckpointFactor());
    }

    /**
     * Check if a message was sent by the primary.
     * @param m -- the message.
     * @return true if was sent by the primary.
     */
    public boolean wasSentByPrimary(PBFTServerMessage m){
         return isPrimary(m.getReplicaID(), getCurrentViewNumber());
    }

    public boolean wasSentByPrimary(PBFTServerMessage m, int view){
         return isPrimary(m.getReplicaID(), view);
    }

    public boolean wasSentByAGroupMember(PBFTServerMessage m){
        Object senderID = m.getReplicaID();

        for(int i = 0 ; i < group.getMembers().size(); i ++){

            IProcess p = (IProcess) group.getMembers().get(i);

            if(p.getID().equals(senderID)){
                return true;
            }

        }

        return false;
    }

    AfterEventtable  afterEventtable  = new AfterEventtable();
    BeforeEventtable beforeEventtable = new BeforeEventtable();

    public void addListener(IEventListener listener, Method m){
        
        if(listener instanceof IAfterEventListener){
            IAfterEventListener after = (IAfterEventListener)listener;
            ArrayList<IAfterEventListener> afters = afterEventtable.get(m);
            if(afters == null){
                afters = new ArrayList<IAfterEventListener>();
            }
            afters.add(after);
            afterEventtable.put(m, afters);
        }

        if(listener instanceof IBeforeEventListener){
            IBeforeEventListener before = (IBeforeEventListener)listener;
            ArrayList<IBeforeEventListener> befores = beforeEventtable.get(m);
            if(befores == null){
                befores = new ArrayList<IBeforeEventListener>();
            }
            befores.add(before);
            beforeEventtable.put(m, befores);
        }

    }

    public static IPBFTServer create(){
            PBFTServer pbft = new PBFTServer();
            return pbft;//(IPBFTServer)EventHandler.newInstance(pbft, pbft.beforeEventtable, pbft.afterEventtable);
    }

    private PBFTServer(){}

    public long getCurrentPrePrepareSEQ() {return getStateLog().getNextPrePrepareSEQ() -1;}

    public long getCurrentExecuteSEQ() { return getStateLog().getNextExecuteSEQ() - 1;}

//    public long getCurrentPrepareSEQ() { return getStateLog().getNextPrepareSEQ() - 1;}
//
//    public long getCurrentCommitSEQ() { return getStateLog().getNextCommitSEQ() - 1;}

    public int getNextViewNumber(){ return getStateLog().getNextViewNumber();}
    public void setNextViewNumber(int view){ getStateLog().setNextViewNumber(view);}

}