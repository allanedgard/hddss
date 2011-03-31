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
import br.ufba.lasid.jds.adapters.EventHandler;
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
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTProcessingToken;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.util.DigestList;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.util.JDSUtility;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.comm.MessageCollection;
import br.ufba.lasid.jds.comm.MessageQueue;
import br.ufba.lasid.jds.decision.IVote;
import br.ufba.lasid.jds.decision.Quorum;
import br.ufba.lasid.jds.decision.VoteList;
import br.ufba.lasid.jds.group.decision.Vote;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequestInfo;
import br.ufba.lasid.jds.jbft.pbft.server.decision.BagSubject;
import br.ufba.lasid.jds.jbft.pbft.server.decision.CheckpointSubject;
import br.ufba.lasid.jds.jbft.pbft.server.decision.CommitSubject;
import br.ufba.lasid.jds.jbft.pbft.server.decision.MetaDataSubject;
import br.ufba.lasid.jds.jbft.pbft.server.decision.PrepareSubject;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTServerMessageSequenceNumberComparator;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTTimeoutDetector;
import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.state.managers.IRecovarableStateManager;
import br.ufba.lasid.jds.util.IPayload;
import br.ufba.lasid.jds.util.ISchedule;
import br.ufba.lasid.jds.util.XMath;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
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

        long lowWater = getCheckpointLowWaterMark();
        long startSEQ = lowWater+1;
        long finalSEQ = seqn;
        long execSEQ = getStateLog().getNextExecuteSEQ() - 1;

        for(long currSEQ = startSEQ; currSEQ <= finalSEQ; currSEQ ++){
            /* If the sequence number was executed by the replica then we'll be able to compute the checkpoint. */
            if(currSEQ <= execSEQ){

                Quorum q = getStateLog().getQuorum(CHECKPOINTQUORUMSTORE, String.valueOf(currSEQ));

                if(q == null){
                   continue;
                }

                CheckpointSubject decision = (CheckpointSubject)q.getCurrentDecision();

                if(decision != null){

                   long   cSEQN = ( Long ) decision.getInfo(CheckpointSubject.SEQUENCENUMBER);
                   String cDGST = (String) decision.getInfo(CheckpointSubject.DIGEST);

                    CheckpointLogEntry clogEntry = rStateManager.getLogEntry(cSEQN);

                    if(!(clogEntry != null && clogEntry.getDigest() != null && clogEntry.getDigest().equals(cDGST))){
                        break;
                    }

                    if(!clogEntry.wasProcessed()){
                        try {
                            rStateManager.setCurrentState(clogEntry.getState());
                            rStateManager.checkpoint(clogEntry.getCheckpointID());
                            rStateManager.removeLogEntry(clogEntry.getCheckpointID());
                            lowWater = currSEQ;

                        } catch (Exception ex) {
                            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

        if(lowWater >= getStateLog().getCheckpointLowWaterMark()){
            getStateLog().setCheckpointLowWaterMark(lowWater);

            JDSUtility.debug(
               "[PBFTServer:doCheckpoint(seqn)] s" + getLocalServerID() + ", at time " + getClockValue() + ", starts the garbage collection " +
               "procedure with LCWM = " + lowWater + "..."
            );

            /* clean-up the request log */
            getRequestInfo().garbage(lowWater);
            
            /* clean-up the order state log */
            getStateLog().garbage(lowWater);

            JDSUtility.debug(
               "[PBFTServer:doCheckpoint(seqn)] s" + getLocalServerID() + " complete the garbage collection procedure for LCWM = " + lowWater + "!"
            );
            
        }else{
            JDSUtility.debug(
               "[PBFTServer:doCheckpoint(seqn)] s" + getLocalServerID() + ", at time " + getClockValue() + ", does not have cached stated matching " +
               "LCWM = " + seqn + "..."
             );
        }
    }
        
  /*########################################################################
   # 1. Methods for handling client requests.
   #########################################################################*/
    public void handle(PBFTRequest r){
       
      Object lServerID = getLocalServerID();

      JDSUtility.debug("[PBFTServer:handle(request)] s" + lServerID + ", at time " + getClockValue() + ", received " + r);

      /* if it is a new request then it'll be accepted */
      if(isValid(r)){
         
         try{
            
            JDSUtility.debug("[PBFTServer:handle(request)] s" + lServerID + ", at time " + getClockValue() + ", accepted " + r + " as a new request.");

            String digest = getAuthenticator().getDigest(r);

            /* it's a new request, so it'll be accepted and put it in backlog state. */
            getRequestInfo().add(digest, r, StatedPBFTRequestMessage.RequestState.WAITING);

            JDSUtility.debug("[PBFTServer:handle(request)] s" + lServerID + " inserted " + r + " in waiting state.");

            /* performs the batch procedure if the server is the primary replica. */
            if(isPrimary()){
               JDSUtility.debug("[PBFTServer:handle(request)] s" + lServerID + " (primary) is executing the batch procedure for " + r + ".");
               batch();
            }else{
               /* schedules a timeout for the arriving of the pre-prepare message if the server is a secundary replica. */
               scheduleViewChange();
            }//end if is primary
            
         } catch (Exception ex) {
            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
         }//end try/catch         
      }//end if can proceed (request)
    }//end handle(request)

    /**
     * Checks if the client request r is new it returns true, otherwise: (a) If r is old, belongs to log and was served 
     * then its reply will be resend (b) if r is old, doesn't belogns to log then a null reply will be sent.
     * @param r - the client request
     * @return true if it is a new request.
     */
    public boolean isValid(PBFTRequest r){
       
      PBFTRequestInfo rinfo = getRequestInfo(); 
      Object          lsid  = getLocalServerID();

      /* checks if it has been logged. */
      if(rinfo.logged(r)){

         /* checks if was served . */
         if(rinfo.wasServed(r)){
            JDSUtility.debug("[PBFTServer:canProceed(request)] s" + lsid + " has already served " + r);
            
            /* retransmite the reply when the request was already served */
            emit(rinfo.getReply(r), new BaseProcess(r.getClientID()));
            return false;
         }
         /* if it was logged but it hasn't been server yet then it'll be discarded because it has been processed. */
         JDSUtility.debug("[PBFTServer:canProceed(request)] s" + lsid + " has already accepted " + r + " so this was discarded.");
         return false;
      }

      /* if it isn't logged but it is older than some logged requests then it was discarded by the garbage collector. Thus, the
      replica must sent a null reply */
      if(rinfo.isOld(r)){
         JDSUtility.debug("[PBFTServer:canProceed(request)] s" + lsid + " hasn't a response for  " + r + " any more.");
         emit(createNullReplyMessage(r), new BaseProcess(r.getClientID()));
         return false;
      }

      return true;
   }
    
   public void batch(){
      if(isPrimary()){
         if(hasACompleteBatch()){
            emitBatch();
         }else{
            scheduleSendBatch();
         }//end if has a complete batch
      }//end if is primary
   }//end batch(digest)

    protected boolean hasACompleteBatch(){
       return getBatchSize() < getRequestInfo().getSizeInBytes();
    }//is a complete batch

   protected void emitBatch(){
      synchronized(this){

         revokeSendBatch();

         int viewn = getCurrentViewNumber();
         long seqn = getStateLog().getNextPrePrepareSEQ();

         PBFTRequestInfo rinfo = getRequestInfo();

         /* creates a new pre-prepare message */
         PBFTPrePrepare pp = new PBFTPrePrepare(viewn, seqn, getLocalServerID());

         int size = 0;

         /* while has not achieved the batch size and there is digests in queue */
         while(size < getBatchSize() && !rinfo.digestQueueIsEmpty()){
            String digest = rinfo.getDigestFromQueue();
            pp.getDigests().add(digest);
            size += rinfo.getRequestSize(digest);
         }

         /* emits pre-prepare */
         emit(pp, getLocalGroup().minus(getLocalProcess()));

         /* update log current pre-prepare */
         getDecision(pp);

         /* if there is digest then it will schedule a send batch */
         if(!rinfo.digestQueueIsEmpty()){
            scheduleSendBatch();
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
               JDSUtility.debug("[PBFTServer::PBFTTimeoutDetector:onTimeout] s" + getLocalServerID() + " had a batch timer expired at time " + getClockValue());
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
            JDSUtility.debug("[PBFTServer:doSchedule(digest)] s" + getLocalServerID() + " scheduled a batch timeout for " + getBatchTimer().getTimestamp());

        }//end batch time is no working
    }//end scheduleSendBatch()

    /* change-view timer */
    protected ISchedule vtimer = null;
    
    protected ISchedule getViewTimer(){
        if(vtimer == null){
             PBFTTimeoutDetector ttask = new PBFTTimeoutDetector() {
                @Override
                public void onTimeout() {
                    emitChangeView();
                }
            };
            vtimer = getScheduler().newSchedule();
            vtimer.setTask(ttask);
            ttask.put("TIMEOUT", getPrimaryFaultTimeout());
        }//end if change-view timer is null
        return vtimer;        
    }//end getViewTimer():ISchedule
   /**
    * Schedule a view change in case of late response for the primary.
    * @param request - the client request.
    * @param timeout - the view change timeout.
    */
   public void scheduleViewChange(){
      if(!isPrimary()){
         long now = getClockValue();

         if(getViewTimer().workingAt(now)){

            long timeout = getPrimaryFaultTimeout();
            PBFTTimeoutDetector ttask = (PBFTTimeoutDetector) getViewTimer().getTask();

            if(ttask != null && ttask.get("TIMEOUT") != null){
               timeout = (Long)ttask.get("TIMEOUT");
            }

            long timestamp = now + timeout;
            getViewTimer().schedule(timestamp);
         }//end change-view timer is working
      }//end if it is not primary
      
   }//scheduleViewChange()

    /**
     * revoke the timer assigned to a client request (i.e. the change view timer).
     * @param leafPartDigest
     */
    public void revokeViewChange(){
       if(!isPrimary()){
         getViewTimer().cancel();
       }
    }//end revokeViewChange()

 /*########################################################################
  # 2. Methods for handling pre-prepare messages.
  #########################################################################*/
   public void handle(PBFTPrePrepare pp){
      JDSUtility.debug("[PBFTServer:handle(preprepare)] s" + getLocalServerID() + ", at time " + getClockValue() + ", received " + pp);

      if(isValid(pp)){

         pp = getDecision(pp);
         
         if(pp != null){
            if(!isPrimary()){
               PBFTPrepare p = createPrepareMessage(pp);
               emit(p, getLocalGroup().minus(getLocalProcess()));
               getDecision(p);
            }
         }
      }
   }

    public boolean isValid(PBFTPrePrepare pp){
       Object lServerID = getLocalServerID();
        /* If the preprepare hasn't a valid sequence / view number then it'll force a change view. */
        if(!(checkSequenceNumber(pp) && checkViewNumber(pp))){
            long nxPP = getStateLog().getNextPrePrepareSEQ();  long viewn  = getCurrentViewNumber();
            JDSUtility.debug(
              "[PBFTServer:canProceed(preprepare)] s" + lServerID + ", at time " + getClockValue() + ", discarded " + pp + " because it hasn't " +
              "a valid sequence/view number. (current(viewn) = " + viewn + ") [nextSEQN(pre-prepare) = " + nxPP + "]"
            );

            return false;
        }

        /* If the preprepare message wasn't sent by the primary replica then it will be discarded. */
        if(!wasSentByPrimary(pp)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(preprepare)] s" + lServerID + ", at time " + getClockValue() + ", discarded " + pp + " " +
              "because it wasn't sent by primary server s" + getCurrentPrimaryID()
            );
            
            return false;
        }
        return true;
    }

   public PBFTPrePrepare getDecision(PBFTPrePrepare pp){
      Object lServerID = getLocalServerID();
      
      /* If the preprepare is null then do nothing. */
      if(pp != null){
         PBFTRequestInfo rinfo = getRequestInfo();

         /* Get composite key of the prepare. */
         Long entryKey = pp.getSequenceNumber();

         /* If the entry key is diferent of null then update state. Otherwise do nothing. */
         if(entryKey != null) {

            /* Get the batch in the preprepare. */
            DigestList digests = pp.getDigests();

            /* For each request in batch, check if such request was received. */
            for(String digest : digests){
               if(!rinfo.isWaiting(digest)){
                  JDSUtility.debug(
                  "[PBFTServer:updateStatus(preprepare)] s" +lServerID + ",  at time " + getClockValue() + ", couldn't update " +
                  "pre-prepare " + pp + " because it has a digest (" + digest + ") that hasn't been in waiting state anymore."
                  );
                  return null;
               }
            }//end for each request in pre-prepare

            /* get a log  entry for current preprepare. */
            PBFTLogEntry entry = getStateLog().get(entryKey);

            /* if there isn't a entry then create one. */
            if(entry == null){

               entry = new PBFTLogEntry(pp);

               /* Update the entry in log. */
               getStateLog().put(entryKey, entry);

               JDSUtility.debug(
                  "[PBFTServer:updateStatus(preprepare)] s"  + lServerID + ", at time " + getClockValue() + ", " +
                  "created a new entry in its log for " + pp
               );

               revokeViewChange();

               /* For each request in batch, check if such request was received. */
               for(String digest : pp.getDigests()){
                  rinfo.assign(digest, StatedPBFTRequestMessage.RequestState.PREPREPARED);
                  rinfo.assign(digest, pp.getSequenceNumber());
               }//end if for each digest

               if(rinfo.hasSomeWaiting()){
                  scheduleViewChange();
               }

               getStateLog().updateNextPrePrepareSEQ(pp);

               return pp;
               
            }//end if entry is null
         }//end if entrykey is not null
      }//end if pp is not null

      return null;
      
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
      JDSUtility.debug("[PBFTServer:handle(prepare)] s" + getLocalServerID() + ", at time " + getClockValue() + ", received " + p);
      if(isValid(p)){

         PrepareSubject ps = getDecision(p);
         
         if(ps != null){
            long seqn = ( Long  ) ps.getInfo(PrepareSubject.SEQUENCENUMBER);
            int viewn = (Integer) ps.getInfo(PrepareSubject.VIEWNUMBER);
            
            PBFTCommit commit = createCommitMessage(viewn, seqn);

            emit(commit, getLocalGroup().minus(getLocalProcess()));
            getDecision(commit);
         }
      }
   }

    public boolean isValid(PBFTPrepare p){
       Object lServerID = getLocalServerID();
        /* If the prepare message was sent by the primary then it will be discarded. */
        if(wasSentByPrimary(p)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(prepare)] s" + lServerID + ", at time " + getClockValue() + ", discarded " + p + " " +
              "because it was sent by the primary " + getCurrentPrimaryID()
            );

            return false;
        }

        /* If the preprepare message wasn't sent by a group member then it will be discarded. */
        if(!wasSentByAGroupMember(p)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(prepare)] s" + lServerID + ", at time " + getClockValue() + ", discarded " + p + " because " +
              "it wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }
        
        /* If the preprepare hasn't a valid sequence or view number then force a change view. */
        if(!checkViewNumber(p)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(prepare)] s" + lServerID + ", at time " + getClockValue() + ", discarded " + p + " because " +
              "it hasn't a valid view number. (CURRENT-VIEW = " + getCurrentViewNumber() + ")."
            );
            return false;
        }

        /* If the preprepare hasn't a valid sequence or view number then force a change view. */
        if(!inAValidSequenceRange(p)){
            long lcwm = getCheckpointLowWaterMark(); long hcwm = getCheckpointHighWaterMark();
            JDSUtility.debug(
              "[PBFTServer:canProceed(prepare)] s" + lServerID + ", at time " + getClockValue() + ", discarded " + p + " because " +
              "it hasn't a valid sequence (LCWM = " + lcwm + ", HCWM = " + hcwm + ")."
            );            
            return false;
        }

        PBFTRequestInfo rinfo = getRequestInfo();

        if(!rinfo.wasPrePrepared(p.getSequenceNumber())){
            JDSUtility.debug(
              "[PBFTServer:canProceed(prepare)] s" + lServerID + ", at time " + getClockValue() + ", discarded " + p + " because " +
              "it hasn't received a related pre-prepare."
            );
            return false;
        }

        return true;
    }

    public PBFTCommit createCommitMessage(int viewn, long seqn){
       return new PBFTCommit(viewn, seqn, getLocalServerID());
    }
    public PBFTCommit createCommitMessage(PBFTPrepare p){
        PBFTCommit c = new PBFTCommit(p, getLocalServerID());
        return c;
    }

   public PrepareSubject getDecision(PBFTPrepare p){
      Object lServerID = getLocalServerID();

      boolean completed = false;
      /* If the preprepare is null then do nothing. */
      if(p != null){

         /* Get composite key of the prepare. */
         Long entryKey = p.getSequenceNumber();

         /* If the entry key is diferent of null then update state. Otherwise do nothing. */
         if(entryKey != null) {

            PBFTRequestInfo rinfo = getRequestInfo();

            if(!rinfo.wasPrePrepared(p.getSequenceNumber())){
               return null;
            }

            /* get a log  entry for current preprepare. */
            PBFTLogEntry entry = getStateLog().get(entryKey);

            /* if there isn't a entry then create one. */
            if(entry != null){

               Quorum q = entry.getPrepareQuorum();

               if(q == null){
                  int f = getServiceBFTResilience(); 
                  q = new Quorum(2 * f);
                  entry.setPrepareQuorum(q);
                  JDSUtility.debug("[PBFTServer:updateState(prepare)] s"  + lServerID + ", at time " + getClockValue() + ", created a new certificate for " + p);
               }
               /*TODO: evaluate if "q.getCurrentDecision()" is a better implementation */
               PrepareSubject decision = (PrepareSubject)q.decide();

               if(decision != null){
                  completed = true;
               }

               q.add(new Vote(p.getReplicaID(), new PrepareSubject(p)));

               JDSUtility.debug(
                  "[PBFTServer:updateState(prepare)] s" + lServerID + ", at time " + getClockValue() + ", inserted " + p + " in the local quorum certicate."
               );

               if(decision == null){
                  decision = (PrepareSubject)q.decide();
               }

               //Long seqn = p.getSequenceNumber();

               if(!completed && decision != null){

                  long seqn = ( Long  ) decision.getInfo(PrepareSubject.SEQUENCENUMBER);
                  int viewn = (Integer) decision.getInfo(PrepareSubject.VIEWNUMBER);
                  rinfo.assign(seqn, StatedPBFTRequestMessage.RequestState.PREPARED);

                  JDSUtility.debug(
                     "[PBFTServer:accept(prepare)] s" + lServerID + ", at time " + getClockValue() + ", completed prepare phase for sequence number (" + seqn + ") " +
                     "and view number (" + viewn + ")."
                  );

                  getStateLog().updateNextPrepareSEQ(p);

                  /* Update the entry in log. */
                  getStateLog().put(entryKey, entry);

                  return decision;
               }

            }
         }
      }
      return null;

   }


/*########################################################################
  # 4. Methods for handling commit messages.
  #########################################################################*/
    public void handle(PBFTCommit c){

        JDSUtility.debug("[PBFTServer:handle(commit)] s" + getLocalServerID() + ", at time " + getClockValue() + ", received " + c);
        if(isValid(c)){
           CommitSubject cs = getDecision(c);
            if(cs != null){

               long seqn = ( Long  ) cs.getInfo(CommitSubject.SEQUENCENUMBER);
              int viewn = (Integer) cs.getInfo(CommitSubject.VIEWNUMBER);
              
               handle(new PBFTProcessingToken(viewn, seqn));
            }
        }
    }

    public boolean isValid(PBFTCommit c){
        /* If the preprepare message wasn't sent by a group member then it will be discarded. */
        if(!wasSentByAGroupMember(c)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(commit)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + c + " because " +
              "it wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }

        PBFTRequestInfo rinfo = getRequestInfo();
        
        if(!rinfo.wasPrepared(c.getSequenceNumber())){
            JDSUtility.debug(
              "[PBFTServer:canProceed(commit)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + c + " because " +
              "it hasn't received a related prepare."
            );

            return false;
        }

        /* If the preprepare hasn't a valid sequence or view number then force a change view. */
        if(!checkViewNumber(c)){

            JDSUtility.debug(
              "[PBFTServer:canProceed(commit)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + c + " because " +
              "it hasn't a valid view number (CURRENT-VIEW = " + getCurrentViewNumber() + ")."
            );
            return false;
        }

        /* If the preprepare hasn't a valid sequence or view number then force a change view. */
        if(!inAValidSequenceRange(c)){
            long lcwm = getCheckpointLowWaterMark(); long hcwm = getCheckpointHighWaterMark();
            JDSUtility.debug(
              "[PBFTServer:canProceed(commit)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + c + " because it " +
              "hasn't a valid sequence number (LCWM = " + lcwm + ", HCWM = " + hcwm + ")."
            );

            return false;

        }
        return true;

    }

    public CommitSubject getDecision(PBFTCommit c){
       boolean completed = false;
      /* If the preprepare is null then do nothing. */
      if(c != null){;

         /* Get composite key of the prepare. */
         Long entryKey = c.getSequenceNumber();

         /* If the entry key is diferent of null then update state. Otherwise do nothing. */
         if(entryKey != null) {

             /* get a log  entry for current preprepare. */
             PBFTLogEntry entry = getStateLog().get(entryKey);

             /* if there isn't a entry then create one.*/
             if(entry != null){

                 Quorum pq = entry.getPrepareQuorum();

                 if(pq != null && pq.getCurrentDecision() != null){

                    PrepareSubject pqd = (PrepareSubject)pq.getCurrentDecision();

                    int viewn = (Integer) pqd.getInfo(PrepareSubject.VIEWNUMBER);
                    
                    if(!c.getViewNumber().equals(viewn)){
                       return null;
                    }

                     Quorum q = entry.getCommitQuorum();

                     if(q == null){

                         int f = getServiceBFTResilience();

                         q = new Quorum(2 * f + 1);

                         entry.setCommitQuorum(q);
                     }

                     /*TODO: evaluate if "q.getCurrentDecision()" is a better implementation */
                     CommitSubject decision = (CommitSubject) q.decide();

                     if(decision != null){
                        completed = true;
                     }

                     q.add(new Vote(c.getReplicaID(), new CommitSubject(c)));
                     
                     JDSUtility.debug(
                       "[PBFTServer:updateState(commit)] s" + getLocalServerID() + ", at time " + getClockValue() + ", inserted " + c +
                       " in the local quorum certicate."
                     );


                     if(decision == null){
                        decision = (CommitSubject)q.decide();
                     }

                     /* Update the entry in log. */
                     getStateLog().put(entryKey, entry);

                     if(!completed && decision != null){

                        long seqn = (Long) decision.getInfo(CommitSubject.SEQUENCENUMBER);
                        
                        PBFTRequestInfo rinfo = getRequestInfo();
                        
                        rinfo.assign(seqn, StatedPBFTRequestMessage.RequestState.COMMITTED);
                        
                        getStateLog().updateNextCommitSEQ(c);

                        JDSUtility.debug(
                           "[PBFTServer:accept(commit)] s" + getLocalServerID() + ", at time " + getClockValue() + ", completed commit phase " +
                           "for sequence number (" + seqn + ") and view number (" + viewn + ")."
                        );


                         return decision;
                     }
                 }
             }
         }
      }
      return null;
    }

 /*########################################################################
  # 5. Methods for handling checkpoint messages.
  #########################################################################*/
    public void handle(PBFTCheckpoint checkpoint){

        JDSUtility.debug("[PBFTServer:handle(checkpoint)] s" + getLocalServerID() + ", at time " + getClockValue() + ", received " + checkpoint);

        if(isValid(checkpoint)){
           
            CheckpointSubject cs = getDecision(checkpoint);
            
            if(cs != null){

                long hcwm = getCheckpointHighWaterMark();
                long seqn = (Long) cs.getInfo(CheckpointSubject.SEQUENCENUMBER);//checkpoint.getSequenceNumber();

                if(seqn > hcwm){
                    JDSUtility.debug(
                      "[PBFTServer:handle(checkpoint)] s" + getLocalServerID() + ", at time " + getClockValue() + ", detected a stable checkpoint " +
                      "certificate with sequence number (" + seqn + ") " + "greater than its high checkpoint water mark (HCWM = " + hcwm + ")."
                    );
                    JDSUtility.debug(
                      "[PBFTServer:handle(checkpoint)] s" + getLocalServerID() + ", at time " + getClockValue() + ", is going to start a start transfer procedure."
                    );

                    emitFetch();
                    return;
                }

                CheckpointLogEntry clogEntry = rStateManager.getLogEntry(seqn);
                if(clogEntry != null){
                    doCheckpoint(seqn);
                    return;
                }//end if ctupe != null
                
                JDSUtility.debug(
                  "[PBFTServer:handle(checkpoint)] s" + getLocalServerID() + ", at time " + getClockValue() + ", is going to fetch the current state " + 
                  "because it hasn't a cached state for the stable checkpoint (" + seqn + ")."
                );
                
                /* We must work better on this after we finish change view procedure. */
                emitFetch();
                                
            }//end if getDecision(checkpoint)

        }//end if getDecision(checkpoint)
        
    }//end handle(checkpoint);
    
    public boolean isValid(PBFTCheckpoint checkpoint){
      /* If the preprepare message wasn't sent by a group member then it will be discarded. */
      if(!wasSentByAGroupMember(checkpoint)){
          JDSUtility.debug(
            "[PBFTServer:canProceed(checkpoint)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + checkpoint + " because " +
            "it wasn't sent by a member of the group " + getLocalGroup()
          );

          return false;
      }//end if wasSentByAGroupMember(checkpoint)

      /* If the preprepare message wasn't sent by a group member then it will be discarded. */
      long lcwm = getCheckpointLowWaterMark();
      long seqn = checkpoint.getSequenceNumber();

      if(lcwm > seqn){
          JDSUtility.debug(
            "[PBFTServer:canProceed(checkpoint)] s" + getLocalServerID() + ", at  time " + getClockValue() + ", discarded " + checkpoint + " because " +
            "it has a sequence number < current LCWM = " + lcwm + "). "
          );

          return false;
      }//end if lcwm  > seqn
      return true;
        
    }//end getDecision(checkpoint)

    /**
     * Update the state of the PBFT. Insert the pre-prepare message in the log entry.
     * @param m
     */
    public CheckpointSubject getDecision(PBFTCheckpoint c){
         boolean completed = false;
         Object lServerID = getLocalServerID();
        /* If the preprepare is null then do nothing. */
        if(c == null) return null;

        /* Get composite key of the prepare. */
        Long entryKey = c.getSequenceNumber();

        /* If the entry key is not null then it'll update state */
        if(entryKey != null) {

            Quorum q = getStateLog().getQuorum(CHECKPOINTQUORUMSTORE, entryKey.toString());

            if(q == null){

                int f = getServiceBFTResilience();

                q = new Quorum(2 * f + 1);

                getStateLog().getQuorumTable(CHECKPOINTQUORUMSTORE).put(entryKey.toString(), q);
            }
            
            /*TODO: evaluate if "q.getCurrentDecision()" is a better implementation */
            CheckpointSubject decision = (CheckpointSubject)q.decide();

            if(decision != null){
               completed = true;
            }

            q.add(new Vote(c.getReplicaID(), new CheckpointSubject(c)));
            
            JDSUtility.debug("[PBFTServer:updateState(checkpoint)] s"  + lServerID + ", at time " + getClockValue() + ", updated a entry in its log for " + c);


            if(decision == null){
               decision = (CheckpointSubject) q.decide();
            }

            if(!completed && decision != null){
                long seqn = (Long) decision.getInfo(CheckpointSubject.SEQUENCENUMBER);
                
                JDSUtility.debug("[PBFTServer:updateState(checkpoint)] s" + lServerID + ", at time " + getClockValue() + ", completed quorum for checkpoint (" + seqn + ").");

                return decision;
            }

        }
        
        return null;

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
    }//end handle received meta-data

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

        /* get the part with the maximum record index (this is, in depthest level)*/
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
            /*get next*/
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
    
    public void doRollback() throws Exception{
        /* rollback to last stable checkpoint */
        rStateManager.rollback();

        /* gets the last stable checkpoint, or zero if it doesn't exist */
        long seqn = rStateManager.getCurrentCheckpointID();

        IRecoverableServer lServer = (IRecoverableServer)getServer();

        if(seqn >= 0){ //tuple!=null
            getStateLog().setNextPrePrepareSEQ(seqn+1);
            getStateLog().setNextPrepareSEQ(seqn+1);
            getStateLog().setNextCommitSEQ(seqn+1);
            getStateLog().setNextExecuteSEQ(seqn+1);

            getStateLog().setCheckpointLowWaterMark(seqn);

            lServer.setCurrentState(rStateManager.getCurrentState());
            updateCurrentSequenceNumber(seqn);
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
              "[PBFTServer:canProceed(fetch)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + f + " " +
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
        long lcwm = getCheckpointLowWaterMark();
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
              "[PBFTServer:canProceed(data)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + d +
              " because it there isn't in temporary part table."
            );

            return false;
        }

        return true;
    }


    public boolean isValid(PBFTMetaData md){
        if(!wasSentByAGroupMember(md)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(metadata)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + md +
              " because it wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }
        
        long lcwm = getCheckpointLowWaterMark();
        long rcmd = md.getCheckpoint();

        if(rcmd <  lcwm){
            JDSUtility.debug(
              "[PBFTServer:canProceed(metadata)] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + md + " because it has " +
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

         Quorum q = getStateLog().getQuorum(METADATAQUORUMSTORE, entryKey.toString());

         if(q == null){
            int f = getServiceBFTResilience();

            q = new Quorum(2 * f + 1);

            getStateLog().getQuorumTable(METADATAQUORUMSTORE).put(entryKey.toString(), q);
         }

         /*TODO: evaluate if "q.getCurrentDecision()" is a better implementation */
         MetaDataSubject decision = (MetaDataSubject)q.decide();

         if(decision != null){
            completed = true;
         }

         q.add(new Vote(md.getReplicaID(), new MetaDataSubject(md)));

         JDSUtility.debug("[PBFTServer:updateState(metadata)] s" + lServerID + ", at time " + getClockValue() + ", updated a entry in its log for " + md);


         if(decision == null){
            decision = (MetaDataSubject)q.decide();
         }

         if(!completed && decision != null){
            long cseqn = (Long) decision.getInfo(MetaDataSubject.CHECKPOINT);
            JDSUtility.debug("[PBFTServer:updateState(metadata)] s" + lServerID + ", at time " + getClockValue() + ", complete a quorum for metada with LCWM (" + cseqn + ").");
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

        JDSUtility.debug(
           "[PBFTServer:handle(bag)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + bag
        );
        if(isValid(bag)){
            BagSubject bd = getDecision(bag);
            if(bd != null){

                Long entryKey = bag.getSequenceNumber();

                Quorum q = getStateLog().getQuorum(BAGQUORUMSTORE, entryKey.toString());
                MessageCollection preprepareList = new MessageCollection();
                MessageCollection prepareList = new MessageCollection();
                MessageCollection commitList = new MessageCollection();
                MessageCollection checkpointList = new MessageCollection();
                for(IVote v : q.getVotes()){
                  BagSubject bs = (BagSubject) v.getSubject();
                  MessageCollection messages  = (MessageCollection)bs.getInfo(BagSubject.MESSAGES);
                 for(IMessage m : messages){
                     if(m instanceof PBFTPrePrepare) preprepareList.add(m);

                     if(m instanceof PBFTPrepare)  prepareList.add(m);

                     if(m instanceof PBFTCommit) commitList.add(m);

                     if(m instanceof PBFTCheckpoint) checkpointList.add(m);
                 }

                }
                
                PBFTServerMessageSequenceNumberComparator comparator =
                        new PBFTServerMessageSequenceNumberComparator();

                Collections.sort(preprepareList, comparator);
                Collections.sort(prepareList, comparator);
                Collections.sort(commitList, comparator);
                Collections.sort(checkpointList, comparator);

                for(IMessage m : preprepareList){
                    PBFTPrePrepare m1 = (PBFTPrePrepare)m;
                    long nextSEQ = getStateLog().getNextPrePrepareSEQ();
                    long currSEQ = m1.getSequenceNumber();
                    if(currSEQ == nextSEQ) handle(m1);
                }
                
                for(IMessage m : prepareList){
                    PBFTPrepare m1 = (PBFTPrepare)m;
                    long nextSEQ = getStateLog().getNextPrePrepareSEQ();
                    long currSEQ = m1.getSequenceNumber();
                    if(currSEQ <= nextSEQ) handle(m1);
                }
                for(IMessage m : commitList){
                    PBFTCommit m1 = (PBFTCommit)m;
                    long nextSEQ = getStateLog().getNextPrepareSEQ();
                    long currSEQ = m1.getSequenceNumber();
                    if(currSEQ <= nextSEQ) handle(m1);
                }
                
                for(IMessage m : checkpointList){
                    PBFTCheckpoint m1 = (PBFTCheckpoint)m;
                    long lastSEQ = getStateLog().getCheckpointLowWaterMark();
                    long currSEQ = m1.getSequenceNumber();
                    if(currSEQ > lastSEQ) handle(m1);
                }                
            }
        }
    }

    public boolean isValid(PBFTBag bag){
        /* If the preprepare message wasn't sent by a group member then it will be discarded. */
        if(!wasSentByAGroupMember(bag)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(bag)] s"   + getLocalServerID() +  ", at " +
              "time " + getClockValue() + ", discarded " + bag +" because it " +
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
         Quorum q = getStateLog().getQuorum(BAGQUORUMSTORE, entryKey.toString());
         if(q == null){
            int f = getServiceBFTResilience();
            q = new Quorum(2 * f + 1);
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

         JDSUtility.debug("[PBFTServer:updateState(bag)] s"  + lServerID +", at time " + getClockValue() + ", updated a entry in its log for " + bag);

         if(!completed && decision != null){
            long seqn = (Long) decision.getInfo(BagSubject.SEQUENCENUMBER);
            JDSUtility.debug("[PBFTServer:updateState(bag] s" + lServerID + ", at time " + getClockValue() + ", completed  quorum for bag with EXEC-SEQ (" + seqn + ").");
            return decision;
         }
      }
      return null;
   }

   /*########################################################################
     # 8. Methods for handling status-active messages.
     #########################################################################*/

    public void handle(PBFTStatusActive sa){
        long lexcSEQ = getStateLog().getNextExecuteSEQ()    -1;
        long lcmtSEQ = getStateLog().getNextCommitSEQ()     -1;
        long lpreSEQ = getStateLog().getNextPrepareSEQ()    -1;
        long lpprSEQ = getStateLog().getNextPrePrepareSEQ() -1;
        long llcwSEQ = getCheckpointLowWaterMark();

        JDSUtility.debug(
           "[PBFTServer:handle(statusactive)] s" + getLocalServerID() + ", at time " + getClockValue() + ", received " + sa + " current " + 
           "(LCWM = " + llcwSEQ + "; PPSEQ = " + lpprSEQ + "; PSEQ = " + lpreSEQ + "; CSEQ = " + lcmtSEQ + "; ESEQ = " + lexcSEQ + ")"
        );

        if(isValid(sa)){
            
            Long maxSEQ = getStateLog().getNextPrePrepareSEQ();
            Long minSEQ = getStateLog().getNextExecuteSEQ()-1;
            
            //remote active state variables
            long rexcSEQ = sa.getLastExecutedSEQ();         
            long rcmtSEQ = sa.getLastCommittedSEQ();        
            long rpreSEQ = sa.getLastPreparedSEQ();         
            long rpprSEQ = sa.getLastPrePreparedSEQ();      
            long rlcwSEQ = sa.getLastStableCheckpointSEQ();

            if(minSEQ > rexcSEQ) minSEQ = rexcSEQ; if(maxSEQ < rexcSEQ) maxSEQ = rexcSEQ;
            if(minSEQ > rcmtSEQ) minSEQ = rcmtSEQ; if(maxSEQ < rcmtSEQ) maxSEQ = rcmtSEQ;
            if(minSEQ > rpreSEQ) minSEQ = rpreSEQ; if(maxSEQ < rpreSEQ) maxSEQ = rpreSEQ;
            if(minSEQ > rpprSEQ) minSEQ = rpprSEQ; if(maxSEQ < rpprSEQ) maxSEQ = rpprSEQ;
            if(minSEQ > rlcwSEQ) minSEQ = rlcwSEQ; if(maxSEQ < rlcwSEQ) maxSEQ = rlcwSEQ;
            
            if(maxSEQ < 0) maxSEQ = 0L;
            
            PBFTBag bag = new PBFTBag(getLocalServerID());

            bag.setSequenceNumber(rexcSEQ);
            
            if(llcwSEQ <= rpprSEQ ){
                for(long i = minSEQ; i < maxSEQ; i++){

                    PBFTLogEntry entry = getStateLog().get(i);
                    if(entry != null){
                        Quorum pq = entry.getPrepareQuorum();
                        Quorum cq = entry.getCommitQuorum();


                        if(rpreSEQ < i && rpprSEQ < i && isPrimary() && entry.getPrePrepare() != null && lpprSEQ >= i){
                            PBFTPrePrepare pp = entry.getPrePrepare();
                            bag.addMessage(pp);
                        }

                        if(rpreSEQ < i && pq != null && lpprSEQ >= i){
                           VoteList votes = pq.getVotes();
                           for(IVote v : votes){
                              Object rid = v.getElector().getID();
                              if(rid.equals(getLocalServerID())){
                                 PrepareSubject ps = (PrepareSubject) v.getSubject();
                                 bag.addMessage(ps.getPrepare());
                              }
                           }
                        }

                        if(rlcwSEQ < i && rcmtSEQ < i && cq != null && lcmtSEQ >=i){
                           VoteList votes = cq.getVotes();
                           for(IVote v : votes){
                              Object rid = v.getElector().getID();
                              if(rid.equals(getLocalServerID())){
                                 CommitSubject cs = (CommitSubject) v.getSubject();
                                 bag.addMessage(cs.getCommit());
                              }
                           }
                        }
                    }//end if entry
                }//end for
            }
            
            long currSEQ = rlcwSEQ + 1;
            
            if(currSEQ < llcwSEQ){
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
            }//end while currSEQ < _lwSEQ, if currSEQ_0 = lwSEQ
            
            if(!bag.isEmpty()){
                emit(bag, new BaseProcess(sa.getReplicaID()));
            }            
        }
    }

    public boolean isValid(PBFTStatusActive sa){
        IProcess rServer = new BaseProcess(sa.getReplicaID());
        if(getLocalServerID().equals(rServer.getID())){
            JDSUtility.debug(
              "[PBFTServer:canProceed(activeStatus)] s" + getLocalServerID() + ", at time " + getClockValue() + ", " +
              "discarded " + sa + " because it was sent by the local replica."
            );
            return false;
        }

        if(!checkViewNumber(sa)){
            long nxPP = getStateLog().getNextPrePrepareSEQ();
            long nxPR  = getStateLog().getNextPrepareSEQ();
            long nxCM  = getStateLog().getNextCommitSEQ();
            long nxEX  = getStateLog().getNextExecuteSEQ();
            long lcwm  = getCheckpointLowWaterMark();

            JDSUtility.debug(
              "[PBFTServer:canProceed(activeStatus)] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", " + "discarded " + sa + " because it " +
              "hasn't a valid view number. (viewn = " + getCurrentViewNumber() + ") " +
              "[PP = " + nxPP + ", PR = " + nxPR + ", CM =" + nxCM + " , EX = " + nxEX + ", LCWM = " + lcwm + "]"
            );
            
            return false;
        }

        if(!wasSentByAGroupMember(sa)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(activeStatus)] s" + getLocalServerID() + ", at time " + getClockValue() + ", " +
              "discarded " + sa + " because it wasn't sent by a member of the group " + getLocalGroup() + "."
            );

            return false;
        }

        return getStateLog().getNextPrepareSEQ() >=0;
    }

    PBFTTimeoutDetector periodStatusTimer = null;
    ISchedule stimer;

    protected ISchedule getStatusTimer(){
        if(stimer == null){
            PBFTTimeoutDetector ttask = new PBFTTimeoutDetector() {
                    @Override
                    public void onTimeout() {

                            emit(
                                    createStatusActiveMessage(),
                                    getLocalGroup().minus(getLocalProcess())
                            );

                        schedulePeriodicStatusSend();

                    }
            };

            stimer = getScheduler().newSchedule();
            stimer.setTask(ttask);
        }

        return stimer;
    }
   public void schedulePeriodicStatusSend() {
        long now = getClockValue();
        long period = getSendStatusPeriod();
        
        getStatusTimer().schedule(now + period);
        
   }

    public PBFTStatusActive createStatusActiveMessage(){

        long ppSEQ = getStateLog().getNextPrePrepareSEQ() - 1L;
        long prSEQ = getStateLog().getNextPrepareSEQ() - 1L;
        long cmSEQ = getStateLog().getNextCommitSEQ()  - 1L;
        long exSEQ = getStateLog().getNextExecuteSEQ() - 1L;
        long lcwm = getCheckpointLowWaterMark();

        return new PBFTStatusActive(getLocalServerID(), getCurrentViewNumber(), ppSEQ, prSEQ, cmSEQ, exSEQ, lcwm);

    }

   /*########################################################################
     # 9. Execute sequence number.
     #########################################################################*/

    public void handle(PBFTProcessingToken proctoken){
        JDSUtility.debug("[PBFTSever:handle(token)] s" + getLocalServerID() + ",  at time " + getClockValue() + ", received " + proctoken);

        if(isValid(proctoken)){
            long startSEQ = getStateLog().getNextExecuteSEQ();
            long finalSEQ = proctoken.getSequenceNumber();

            for(long currSEQ = startSEQ; currSEQ <= finalSEQ; currSEQ ++){
                Quorum cq = getStateLog().getCommitQuorum(currSEQ);

                if(!(cq != null && cq.getCurrentDecision() != null)){
                    return;
                }
                
                IRecoverableServer lServer = (IRecoverableServer)getServer();

                PBFTPrePrepare preprepare = getStateLog().getPrePrepare(currSEQ);
                PBFTRequestInfo rinfo = getRequestInfo();

                for(String digest : preprepare.getDigests()){

                    PBFTRequest request = rinfo.getRequest(digest); //statedReq.getRequest();
                    
                    IPayload result = lServer.executeCommand(request.getPayload());

                    PBFTReply reply = createReplyMessage(request, result);
                    
                    rinfo.assign(digest, StatedPBFTRequestMessage.RequestState.SERVED);
                    rinfo.assign(digest, reply);

                    JDSUtility.debug(
                      "[PBFTServer:handle(token)] s"  + getLocalServerID() + ", at time " + getClockValue() + ", executed " + request +
                      " (currView = " + getCurrentViewNumber() + " / SEQ = " + currSEQ + ")."
                    );
                    
                    JDSUtility.debug(
                      "[PBFTServer:handle(token)] s"  + getLocalServerID() + ", at time " + getClockValue() + ", has the following state " + lServer.getCurrentState()
                    );

                    IProcess client = new BaseProcess(reply.getClientID());
                    emit(reply, client);

                }//end for each leafPartDigest (handle and reply)

                JDSUtility.debug(
                  "[PBFTServer:handle(token)] s"  + getLocalServerID() + ", at time " + getClockValue() + ", after execute SEQN = "  + currSEQ + " " +
                  "has the following state " + lServer.getCurrentState()
                );

                getStateLog().updateNextExecuteSEQ(currSEQ);

                long execSEQ = getStateLog().getNextExecuteSEQ() -1;
                long chkPeriod = getCheckpointPeriod();

                if(execSEQ > 0 && ((execSEQ % chkPeriod) == 0)){
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
                    } catch (Exception ex) {
                        Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }
    }

    public boolean isValid(PBFTProcessingToken token){
        Long currSEQ = token.getSequenceNumber();

        PBFTLogEntry entry = getStateLog().get(currSEQ);

        /* If it isn't preprepared then it won't proceed */
        if(!(entry != null && entry.getPrePrepare() != null)){
            return false;
        }

        Quorum pq = entry.getPrepareQuorum();
        Quorum cq = entry.getCommitQuorum();

        /* If it isn't prepared and committed then it won't proceed */
        if(pq != null && pq.getCurrentDecision() != null && cq != null & cq.getCurrentDecision() != null){
            /*if it was served then it wouldn't proceed */
            PBFTRequestInfo rinfo = getRequestInfo();
            if(!rinfo.wasServed(currSEQ)){
                return true;
            }
        }

        return false;
    }

    public  PBFTReply createReplyMessage(PBFTRequest r, IPayload result){

        return createReplyMessage(r, result, getCurrentViewNumber());

    }

    public PBFTReply createReplyMessage(PBFTRequest r, IPayload result, Integer viewNumber){
         PBFTReply reply = new PBFTReply(r, result, getLocalServerID(), viewNumber);
         return reply;
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

            JDSUtility.debug(
              "[PBFTServer:emit(msg, dest)]s" + getLocalServerID() + " " + sent + " " +
              "" + msg + " at timestamp " + getClockValue() + " to " + remote + "."
            );

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
    boolean uncertainty = false;
    /* (a) a change-view message is emitted because of a suspect of failure of the primary replica. */
    
    public void emitChangeView() {

        int viewn = getCurrentViewNumber();

        JDSUtility.debug(
            "[PBFTServer:emitChangeView()] s" + getLocalServerID() + ", at " +
            "time " + getClockValue() + ", is going to emit a change view " +
            "message for (v + 1 = "  +  (viewn + 1) + ")."
        );

        /* the replica makes sure that no timer is currently working. If the view change was trigged by a suspect of failure of the primary then
         * probably it's been already true.
         */
        getViewTimer().cancel();

        /* the replica moves to the next view. After that, this replica isn't accepting any message from view v < v+1. */
        setCurrentViewNumber(viewn +1);

        PBFTChangeView cv = createChangeViewMessage();

        for(PBFTLogEntry lentry: getStateLog().values()){

            Quorum pq = lentry.getPrepareQuorum();

            if(pq.getCurrentDecision() != null){
                PrepareSubject ps = (PrepareSubject) pq.getCurrentDecision();
                
                long seqn = ( Long  ) ps.getInfo(PrepareSubject.SEQUENCENUMBER);
                int  vwn = (Integer) ps.getInfo(PrepareSubject.VIEWNUMBER);
                DigestList digests = (DigestList) ps.getInfo(PrepareSubject.DIGESTLIST);

                cv.addPrepare(seqn, digests, vwn);
            }else{
                PBFTPrePrepare pp = lentry.getPrePrepare();
                cv.addPrePrepare(pp.getSequenceNumber(), pp.getDigests(), pp.getViewNumber());
            }
        }

        PartEntry centry;
        try {
            centry = rStateManager.getPart(0, 0);
            cv.addCheckpoint(centry.getPartCheckpoint(), centry.getDigest());
        } catch (Exception ex) {
            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
            cv.addCheckpoint(0, "");
        }

        getStateLog().clear();

        emit(cv, getLocalGroup().minus(getLocalProcess()));

        updateState(cv);
        uncertainty = true;

        if(!isPrimary()){
            /*if it isn't the primary then it must send a change-view-ack message to the estimated primary*/
            Object npid = getLocalGroup().next(getCurrentPrimaryID());
            IProcess newPrimary = new BaseProcess(npid);
            for(PBFTChangeView oldCV : digcvtable.values()){
                int oldV = oldCV.getViewNumber();
                Object rid = oldCV.getReplicaID();
                if(oldV == getCurrentViewNumber() && !rid.equals(getLocalServerID())){

                    emit(createChangeViewACKMessage(oldCV), newPrimary);

                }

            }

        }
    }

    public void handle(PBFTChangeView cv) {
        JDSUtility.debug(
           "[PBFTServer:handle(changeview)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + cv 
        );

        if(isValid(cv)){

            Object   rpid    = cv.getReplicaID();        //cv sender id
            IProcess rServer = new BaseProcess(rpid);
            int rviewn = cv.getViewNumber();
            int cviewn = getCurrentViewNumber();

            /* if the change-view message was sent by the current primary and such message has a view number greater than the current view
               number then the replica will move to rview */
            if(isPrimary(rServer) && rviewn > cviewn){
                emitChangeView();
            }
            /*if the state was updated then it's a new view-change message*/
            if(updateState(cv)){
                int f = getServiceBFTResilience();
                Integer mxview = kthMaxLoggedViewNumber(f+1);
                
                /*it has at least f+1 view-changes with a view number greater then or equals to mxview: change to view maxv */
                if(mxview != null && mxview.compareTo(rviewn) > 0){
                    setCurrentViewNumber(mxview-1);
                    emitChangeView();
                    return;
                }

                if(uncertainty && !isPrimary()){
                    mxview = kthMaxLoggedViewNumber(2 * f + 1);

                    /*if it has 2f+1 change-view messages with view greater than or equals rviewn then it'll start a timer to ensure it moves
                      to another view if it doesn't receive the new-view message for rviewn*/
                    if(mxview != null && mxview.compareTo(rviewn)==0){
                          PBFTTimeoutDetector ttask =
                             (PBFTTimeoutDetector) getViewTimer().getTask();
                          
                          /* an exponetial timeout has to be considered to guarantee liveness when the end-to-end delay is too long
                             (it's prevent uncessary view-changes)*/
                          long timeout = (Long) ttask.get("TIMEOUT");
                          ttask.put("TIMEOUT", 2 * timeout);
                          scheduleViewChange();
                          uncertainty = false;                          
                    }
                    
                }
            }
        }//end if it received a valid change-view message
    }
        
    public PBFTChangeViewACK createChangeViewACKMessage(PBFTChangeView cv){
        Object prompterID = cv.getReplicaID();
        Object   senderID = getLocalServerID();
        int         viewn = cv.getViewNumber();
        String     digest = "";
        try {
            digest = getAuthenticator().getDigest(cv);
        } catch (Exception ex) {
            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new PBFTChangeViewACK(viewn, senderID, prompterID, digest);
    }

    public boolean isValid(PBFTChangeView cv){
        if(cv == null) return false;

        MessageCollection mcpp = cv.getPrePrepareSet();
        MessageCollection mcpr = cv.getPrepareSet();

        int mcsize = mcpp.size();

        if(mcpr.size() > mcsize) mcsize = mcpr.size();
        int i = -1; int cview = getCurrentViewNumber();
        while(++i < mcsize){

            int viewn = -1;
            
            if(i < mcpp.size()){
                
                PBFTPrePrepare pp = (PBFTPrePrepare) mcpp.get(i);
                
                viewn = pp.getViewNumber();
                if(viewn > cview){
                    JDSUtility.debug(
                        "[PBFTServer:canProceed(changeview] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + cv + " because it has " +
                        "a component with a invalid view number (VW{" + viewn + "} > CURRVW{" + cview + "})."
                    );
                    return false;
                }
            }

            if(i < mcpr.size()){
                PBFTPrepare p = (PBFTPrepare) mcpr.get(i);
                viewn = p.getViewNumber();
                if(viewn > cview){
                    JDSUtility.debug(
                        "[PBFTServer:canProceed(changeview] s" + getLocalServerID() + ", at time " + getClockValue() + ", discarded " + cv + " because it has " +
                        "a component with a invalid view number (VW{" + viewn + "} > CURRVW{" + cview + "})."
                    );
                    return false;
                }
            }
        }
        
        return true;
    }

    public void handle(PBFTChangeViewACK cva) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void handle(PBFTNewView nwv) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    Hashtable<String, PBFTChangeView> digcvtable = new Hashtable<String, PBFTChangeView>();
    
    public boolean updateState(PBFTChangeView cv){
        try {
            /*stores the receive change view if it has been already stored*/
            String digest = getAuthenticator().getDigest(cv);
            if(!digcvtable.contains(digest)){
                digcvtable.put(digest, cv);
                int viewn = cv.getViewNumber();
                views.add(viewn);
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Integer kthMaxLoggedViewNumber(int k){
        return XMath.kthmax(k, views);
    }

    public PBFTChangeView createChangeViewMessage(){
        PBFTChangeView cv =
             new PBFTChangeView(
                    getCheckpointLowWaterMark(),
                    getCurrentViewNumber(),
                    getLocalServerID()
            );
        return cv;
    }


    public void installNewView() {
        throw new UnsupportedOperationException("Not supported yet.");
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

    /* change-view timeout meansured in milliseconds */
    protected long changeViewTimeout = 1000L;

    public long getChangeViewRetransmissionTimeout(){
        return changeViewTimeout;
    }

    public void setChangeViewRetransmissionTimeout(long newTimeout){
        this.changeViewTimeout = newTimeout;
    }

    /* current view number */
    protected int currentViewNumber = 0;

    public Integer getCurrentViewNumber() {
        return currentViewNumber;
    }

    public void setCurrentViewNumber(Integer newViewNumber){
        this.currentViewNumber = newViewNumber;
    }

    /* currentPrimaryID */
    protected Object currentPrimaryID = null;

    public Object getCurrentPrimaryID(){
        return currentPrimaryID;
    }

    public  void setCurrentPrimaryID(Object newServerID){
        currentPrimaryID = newServerID;
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

    public boolean isPrimary(IProcess p){return isPrimary(p.getID());}

    public boolean isPrimary(Object serverID){return getCurrentPrimaryID().equals(serverID);}

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
        long nextPrepareSEQ = getStateLog().getNextPrepareSEQ();
        //long nextCommitSEQ = getStateLog().getNextCommitSEQ();

        if(m != null && m.getSequenceNumber() != null){

            long seqn = m.getSequenceNumber();

            if(m instanceof PBFTPrePrepare){
                return seqn == nextPrePrepareSEQ;
            }

            if(m instanceof PBFTPrepare){
                return seqn == nextPrepareSEQ;
            }

            if(m instanceof PBFTCommit){
                return seqn <= nextPrepareSEQ; //nextCommitSEQ;
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

        Object view = m.getViewNumber();

        return getCurrentViewNumber().equals(view);

    }


    /**
     * Check if a message has a sequence number between the low and high water marks defined by the checkpoint.
     * @param m -- the message.
     * @return -- true if the sequence number of the message is in the valid
     * range.
     */
    public boolean inAValidSequenceRange(PBFTServerMessage m){

        long seqn = m.getSequenceNumber();
        long low  = getCheckpointLowWaterMark();
        long high = getCheckpointHighWaterMark();

        return seqn > low && seqn <= high;

    }

    public Long getCheckpointLowWaterMark(){return getStateLog().getCheckpointLowWaterMark();}

     Long checkpointFactor;

    public void setCheckpointFactor(Long factor){checkpointFactor = factor;}
    public long getCheckpointFactor(){return checkpointFactor;}

    public Long getCheckpointHighWaterMark(){
        return getStateLog().getCheckpointHighWaterMark(getCheckpointPeriod(), getCheckpointFactor());
    }

    /**
     * Check if a message was sent by the primary.
     * @param m -- the message.
     * @return true if was sent by the primary.
     */
    public boolean wasSentByPrimary(PBFTServerMessage m){ return isPrimary(m.getReplicaID());}

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
            return (IPBFTServer)EventHandler.newInstance(pbft, pbft.beforeEventtable, pbft.afterEventtable);
    }

    private PBFTServer(){}

    public long getCurrentPrePrepareSEQ() {return getStateLog().getNextPrePrepareSEQ() -1;}

    public long getCurrentExecuteSEQ() { return getStateLog().getNextExecuteSEQ() - 1;}

    public long getCurrentPrepareSEQ() { return getStateLog().getNextPrepareSEQ() - 1;}

    public long getCurrentCommitSEQ() { return getStateLog().getNextCommitSEQ() - 1;}


}