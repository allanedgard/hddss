/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.util.Queue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class PBFTRequestInfo {
   Hashtable<String, StatedPBFTRequestMessage>                  dLog = new Hashtable<String, StatedPBFTRequestMessage>();
   Hashtable<Long  , ArrayList<String>>                         nLog = new Hashtable<Long  , ArrayList<String>>();
   Hashtable<Object, Hashtable<Long, StatedPBFTRequestMessage>> rLog = new Hashtable<Object, Hashtable<Long, StatedPBFTRequestMessage>>();
   Hashtable<Object, Long>                                timestamps = new Hashtable<Object, Long>();
   Queue<String>                                              dQueue = new Queue<String>();
   
   public boolean add(String digest, PBFTRequest req, StatedPBFTRequestMessage.RequestState reqState){
      if(!dLog.containsKey(digest)){
         StatedPBFTRequestMessage statedREQ = new StatedPBFTRequestMessage(req, reqState, digest);
         dLog.put(digest, statedREQ);

         if(req != null){
            Hashtable<Long, StatedPBFTRequestMessage> requests = rLog.get(req.getClientID());

            if(requests == null){
               requests = new Hashtable<Long, StatedPBFTRequestMessage>();
               rLog.put(req.getClientID(), requests);
            }

            if(!requests.containsKey(req.getTimestamp())){
               requests.put(req.getTimestamp(), statedREQ);
               timestamps.put(req.getClientID(), req.getTimestamp());
            }
         }
         dQueue.add(digest);

         return true;
      }
      return false;
   }
   
   public boolean add(String digest, PBFTRequest req){
      return add(digest, req, StatedPBFTRequestMessage.RequestState.WAITING);
   }

   public PBFTRequest getRequest(String digest){
      StatedPBFTRequestMessage statedREQ = dLog.get(digest);
      if(statedREQ != null){
         return statedREQ.getRequest();
      }
      return null;
   }

   public PBFTReply getReply(PBFTRequest r){
      Hashtable<Long, StatedPBFTRequestMessage> requests = rLog.get(r.getClientID());
      if(requests != null && !requests.isEmpty()){
         StatedPBFTRequestMessage statedREQ = requests.get(r.getTimestamp());
         if(statedREQ != null){
            return statedREQ.getReply();
         }
      }

      return null;
   }

   public boolean assign(PBFTRequest request, StatedPBFTRequestMessage.RequestState state){
      if(!(request != null && request.getClientID() != null && request.getTimestamp() != null)){
         return false;
      }
      Hashtable<Long, StatedPBFTRequestMessage> requests = rLog.get(request.getClientID());
      if(requests != null && !requests.isEmpty()){
         StatedPBFTRequestMessage statedREQ = requests.get(request.getTimestamp());
         statedREQ.setState(state);
         return true;
      }
      return false;
   }

   public boolean assign(String digest, StatedPBFTRequestMessage.RequestState state){
      StatedPBFTRequestMessage statedREQ = dLog.get(digest);
      if(statedREQ != null){
         statedREQ.setState(state);
         return true;
      }
      return false;
   }

   public boolean assign(Long seqn, StatedPBFTRequestMessage.RequestState state){
      ArrayList<String> digests = nLog.get(seqn);
      if(digests != null && !digests.isEmpty()){
         for(String digest : digests){
            assign(digest, state);
         }

         return true;
      }

      return false;
   }

   public void clear(){
      dLog.clear();
      nLog.clear();
      rLog.clear();
      timestamps.clear();
      dQueue.clear();
   }
   
   public void garbage(Long seqn){
      if(seqn != null){
         ArrayList<Long> seqns = new ArrayList<Long>(nLog.keySet());         
         for(int i = seqns.size()-1; i >= 0; i--){
            Long aSeqn = seqns.get(i);
            if(aSeqn != null && aSeqn.longValue() < seqn.longValue()){
               remove(aSeqn);
            }
         }
      }
   }

   public boolean remove(Long seqn){
      ArrayList<String> digests = nLog.get(seqn);
      if(digests != null && !digests.isEmpty()){
         for(String digest : digests){
            remove(digest);
         }
         nLog.remove(seqn);
         return true;
      }
      return false;
   }

   public boolean remove(String digest){
      StatedPBFTRequestMessage statedREQ = dLog.get(digest);
      if(statedREQ != null){
         PBFTRequest req = statedREQ.getRequest();

         if(req != null && req.getClientID() != null && req.getTimestamp() != null){
            Hashtable<Long, StatedPBFTRequestMessage> requests = rLog.get(req.getClientID());

            if(requests != null && !requests.isEmpty()){
               requests.remove(req.getTimestamp());
            }
         }
         
         dQueue.remove(digest);
         
         dLog.remove(digest);
         return true;
      }
      return false;
   }

   public boolean assign(String digest, PBFTReply reply){
      StatedPBFTRequestMessage statedREQ = dLog.get(digest);
      if(statedREQ != null){
         statedREQ.setReply(reply);
         return true;
      }
      return false;
   }

   public boolean assign(String digest, Long seqn){
      StatedPBFTRequestMessage statedREQ = dLog.get(digest);
      if(statedREQ != null){
         statedREQ.setSequenceNumber(seqn);
         ArrayList<String> digests = nLog.get(seqn);
         if(digests == null){
            digests = new ArrayList<String>();
            nLog.put(seqn, digests);
         }

         if(!digests.contains(digest)){
            digests.add(digest);
         }
         
         return true;
      }
      return false;
   }

   public boolean is(String digest, StatedPBFTRequestMessage.RequestState state){
      StatedPBFTRequestMessage statedREQ = dLog.get(digest);
      return (statedREQ != null && statedREQ.getState().equals(state));
   }

   public boolean is(PBFTRequest request, StatedPBFTRequestMessage.RequestState state){
      Hashtable<Long, StatedPBFTRequestMessage> requests = rLog.get(request.getClientID());
      if(requests != null && !requests.isEmpty()){
         StatedPBFTRequestMessage statedREQ = requests.get(request.getTimestamp());
         return (statedREQ != null && statedREQ.getState().equals(state));
      }

      return false;
   }

   public boolean is(Long seqn,  StatedPBFTRequestMessage.RequestState state){
      ArrayList<String> digests = nLog.get(seqn);
      if(digests != null){
         for(String digest : digests){
            if(!is(digest, state)){
               return false;
            }
         }
      }
      return true;
   }

   public boolean logged(PBFTRequest r){
      return (!(!isWaiting(r) && !wasPrePrepared(r) /*&& !wasPrepared(r) && !wasCommitted(r) && !wasServed(r)*/));
   }

   public boolean isOld(PBFTRequest r){
      if(!(r != null && r.getClientID() != null && r.getTimestamp() != null)){
         return false;
      }
      Long timestamp = timestamps.get(r.getClientID());

      return (timestamp != null && timestamp.longValue() > r.getTimestamp().longValue());
      
   }

   public boolean isNew(PBFTRequest r){
      if(!(r != null && r.getClientID() != null && r.getTimestamp() != null)){
         return false;
      }
      Long timestamp = timestamps.get(r.getClientID());

      if(timestamp == null){
         return true;
      }

      return (timestamp.longValue() < r.getTimestamp().longValue());
   }
   
   
   public boolean isWaiting(String digest){
      return is(digest, StatedPBFTRequestMessage.RequestState.WAITING);
   }

   public boolean isWaiting(PBFTRequest r){
      return is(r, StatedPBFTRequestMessage.RequestState.WAITING);
   }

   public boolean isWaiting(Long seqn){
      return is(seqn, StatedPBFTRequestMessage.RequestState.WAITING);
   }

   public boolean wasPrePrepared(String digest){
      return is(digest, StatedPBFTRequestMessage.RequestState.PREPREPARED) || wasPrepared(digest);
   }

   public boolean wasPrePrepared(PBFTRequest r){
      return is(r, StatedPBFTRequestMessage.RequestState.PREPREPARED) || wasPrepared(r);
   }

   public boolean wasPrePrepared(Long seqn){
      return is(seqn, StatedPBFTRequestMessage.RequestState.PREPREPARED) || wasPrepared(seqn);
   }

   public boolean wasPrepared(String digest){
      return is(digest, StatedPBFTRequestMessage.RequestState.PREPARED) || wasCommitted(digest);
   }

   public boolean wasPrepared(PBFTRequest r){
      return is(r, StatedPBFTRequestMessage.RequestState.PREPARED) || wasCommitted(r);
   }

   public boolean wasPrepared(Long seqn){
      return is(seqn, StatedPBFTRequestMessage.RequestState.PREPARED) || wasCommitted(seqn);
   }

  public boolean wasCommitted(String digest){
      return is(digest, StatedPBFTRequestMessage.RequestState.COMMITTED) || wasServed(digest);
  }

  public boolean wasCommitted(PBFTRequest r){
      return is(r, StatedPBFTRequestMessage.RequestState.COMMITTED) || wasServed(r);
  }

  public boolean wasCommitted(Long seqn){
      return is(seqn, StatedPBFTRequestMessage.RequestState.COMMITTED) || wasServed(seqn);
  }

  public boolean wasServed(String digest){
      return is(digest, StatedPBFTRequestMessage.RequestState.SERVED);
  }

  public boolean wasServed(PBFTRequest r){
      return is(r, StatedPBFTRequestMessage.RequestState.SERVED);
  }

  public boolean wasServed(Long seqn){
      return is(seqn, StatedPBFTRequestMessage.RequestState.SERVED);
  }

  public boolean hasSomeInState(StatedPBFTRequestMessage.RequestState state){
      Collection<StatedPBFTRequestMessage> requests =  dLog.values();
      for(StatedPBFTRequestMessage statedREQ : requests){
         if(statedREQ.getState().equals(state)){
            return true;
         }
      }
      return false;
   }

  public boolean hasSomeInState(Long seqn, StatedPBFTRequestMessage.RequestState state){
      ArrayList<String> digests =  nLog.get(seqn);
      for(String digest : digests){
         if(is(digest, state)){
            return true;
         }
      }
      return false;
   }

   public boolean hasSomeWaiting(){
      return hasSomeInState(StatedPBFTRequestMessage.RequestState.WAITING);
   }

   public boolean hasSomeWaiting(Long seqn){
      return hasSomeInState(seqn, StatedPBFTRequestMessage.RequestState.WAITING);
   }

   public boolean hasSomePrePrepared(){
      return hasSomeInState(StatedPBFTRequestMessage.RequestState.PREPREPARED);
   }

   public boolean hasSomePrePrepared(Long seqn){
      return hasSomeInState(seqn, StatedPBFTRequestMessage.RequestState.PREPREPARED);
   }
   
   public boolean hasSomePrepared(){
      return hasSomeInState(StatedPBFTRequestMessage.RequestState.PREPARED);
   }

   public boolean hasSomePrepared(Long seqn){
      return hasSomeInState(seqn, StatedPBFTRequestMessage.RequestState.PREPARED);
   }

   public boolean hasSomeCommitted(){
      return hasSomeInState(StatedPBFTRequestMessage.RequestState.COMMITTED);
   }

   public boolean hasSomeCommitted(Long seqn){
      return hasSomeInState(seqn, StatedPBFTRequestMessage.RequestState.COMMITTED);
   }

   public boolean hasSomeServed(){
      return hasSomeInState(StatedPBFTRequestMessage.RequestState.SERVED);
   }

   public boolean hasSomeServed(Long seqn){
      return hasSomeInState(seqn, StatedPBFTRequestMessage.RequestState.SERVED);
   }

   public boolean hasSomeMissed(){
      return hasSomeInState(StatedPBFTRequestMessage.RequestState.MISSED);
   }

   public boolean hasSomeMissed(Long seqn){
      return hasSomeInState(seqn, StatedPBFTRequestMessage.RequestState.MISSED);
   }

   public int getQueueSize(){
      return dQueue.size();
   }
   public int getSizeInBytes(){
      int size = 0;

      for(int i = 0; i < dQueue.size(); i++){
         String digest = dQueue.get(i);         
         size += getRequestSize(digest);
      }

      return size;
   }

   public int getRequestSize(String digest){
      PBFTRequest r = getRequest(digest);
      return r == null ? 0 : r.getSize();
   }

   public String getDigestFromQueue(){
      return dQueue.remove();
   }

   public boolean digestQueueIsEmpty(){
      return dQueue.isEmpty();
   }
}
