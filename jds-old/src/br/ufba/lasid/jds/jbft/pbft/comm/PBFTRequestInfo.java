/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import java.util.Hashtable;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage.RequestState;
import br.ufba.lasid.jds.util.DigestList;
import java.util.TreeMap;

/**
 *
 * @author aliriosa
 */
public class PBFTRequestInfo {
   TreeMap<Integer, StatedPBFTRequestMessage> requestLog = new TreeMap<Integer, StatedPBFTRequestMessage>();
   Hashtable<Object, Long>                      sessions = new Hashtable<Object, Long>();
//   Hashtable<String, StatedPBFTRequestMessage>                  dLog = new Hashtable<String, StatedPBFTRequestMessage>();
//   Hashtable<Long  , ArrayList<String>>                         nLog = new Hashtable<Long  , ArrayList<String>>();
//   Hashtable<Object, Hashtable<Long, StatedPBFTRequestMessage>> rLog = new Hashtable<Object, Hashtable<Long, StatedPBFTRequestMessage>>();
   
//   Queue<String>                                              dQueue = new Queue<String>();
   
   public StatedPBFTRequestMessage add(String digest, PBFTRequest req, RequestState reqState){
        for(StatedPBFTRequestMessage statedRequest : requestLog.values()){
           if(statedRequest.getDigest().equals(digest)){
              statedRequest.setState(reqState);
              if(req != null && req.getClientID() != null && req.getTimestamp() != null){
                 statedRequest.setRequest(req);
                 Long currentTimestamp = sessions.get(req.getClientID());

                 if(currentTimestamp == null){
                    currentTimestamp = req.getTimestamp();
                 }
                 
                 if(currentTimestamp.longValue() < req.getTimestamp().longValue()){
                    currentTimestamp = req.getTimestamp();
                 }
                 
                 sessions.put(req, currentTimestamp);
              }
              return statedRequest;
           }
        }

        StatedPBFTRequestMessage statedRequest = new StatedPBFTRequestMessage(req, reqState, digest);
        
        Integer index = null;
        if(!requestLog.isEmpty()){
           index = (Integer)requestLog.lastKey();
        }
        
        if(index == null){
           index = Integer.valueOf(-1);
        }

        index = index + 1;
        requestLog.put(index, statedRequest);

      return statedRequest;
      
   }
   
   public boolean isNewest(PBFTRequest req){

      if(!(req != null && req.getClientID() != null && req.getTimestamp() != null)){
         return false;
      }
     Long currentTimestamp = sessions.get(req.getClientID());
     if(currentTimestamp == null){
        return true;
     }

     if(currentTimestamp.longValue() < req.getTimestamp().longValue()){
        return true;
     }

     return false;

   }
   
   public StatedPBFTRequestMessage add(String digest, PBFTRequest req){
      return add(digest, req, RequestState.WAITING);
   }

   public Long getSequenceNumber(String digest){
      for(StatedPBFTRequestMessage sr : requestLog.values()){
        if(sr.getDigest().equals(digest)){
           return sr.getSequenceNumber();
        }
      }

      return null;
   }

   public PBFTRequest getRequest(String digest){
      for(StatedPBFTRequestMessage sr : requestLog.values()){
        if(sr.getDigest().equals(digest)){
           return sr.getRequest();
        }
      }

      return null;
   }

   public PBFTReply getReply(PBFTRequest r){
      if(!(r != null && r.getClientID() != null && r.getTimestamp() != null)){
         return null;
      }
      
      for(StatedPBFTRequestMessage sr : requestLog.values()){

        PBFTRequest r0 = sr.getRequest();

        if(r0 != null && r0.getClientID() != null && r0.getTimestamp() != null){
           if(r.getClientID().equals(r0.getClientID()) && r.getTimestamp().equals(r0.getTimestamp())){
               return sr.getReply();
           }
        }
      }

      return null;
   }

   public boolean assign(PBFTRequest r, RequestState state){
      if(!(r != null && r.getClientID() != null && r.getTimestamp() != null)){
         return false;
      }

      for(StatedPBFTRequestMessage sr : requestLog.values()){

        PBFTRequest r0 = sr.getRequest();

        if(r0 != null && r0.getClientID() != null && r0.getTimestamp() != null){
           if(r.getClientID().equals(r0.getClientID()) && r.getTimestamp().equals(r0.getTimestamp())){
               sr.setState(state);
               return true;
           }
        }
      }

      return false;
   }

   public boolean assign(String digest, RequestState state){
      
      if(digest == null) return false;

      for(StatedPBFTRequestMessage sr : requestLog.values()){
        if(sr.getDigest().equals(digest)){
           sr.setState(state);
           return true;
        }
      }

      return false;
   }

   public boolean assign(Long seqn, RequestState state){
      boolean ok = false;
      for(StatedPBFTRequestMessage sr : requestLog.values()){
         Long seqn0 = sr.getSequenceNumber();
        if(seqn0 != null && seqn0.equals(seqn)){
           sr.setState(state);
           ok = true;
        }
      }

      return ok;
   }

   public void clear(){
      requestLog.clear();      
   }
   
   public void garbage(Long seqn){

      if(seqn != null){
         TreeMap<Integer, StatedPBFTRequestMessage> tempLog = new TreeMap<Integer, StatedPBFTRequestMessage>(requestLog);
         for(Integer key : tempLog.keySet()){
            StatedPBFTRequestMessage sr = tempLog.get(key);
            Long seqn0 = sr.getSequenceNumber();
            if(seqn0 != null && seqn0.longValue() < seqn.longValue()){
               requestLog.remove(key);
            }
         }
      }
   }

//   public boolean remove(Long seqn){
//      ArrayList<String> digests = nLog.get(seqn);
//      if(digests != null && !digests.isEmpty()){
//         for(String digest : digests){
//            remove(digest);
//         }
//         nLog.remove(seqn);
//         return true;
//      }
//      return false;
//   }

//   public boolean remove(String digest){
//      StatedPBFTRequestMessage statedREQ = dLog.get(digest);
//      if(statedREQ != null){
//         PBFTRequest req = statedREQ.getRequest();
//
//         if(req != null && req.getClientID() != null && req.getTimestamp() != null){
//            Hashtable<Long, StatedPBFTRequestMessage> requests = rLog.get(req.getClientID());
//
//            if(requests != null && !requests.isEmpty()){
//               requests.remove(req.getTimestamp());
//            }
//         }
//
//         dQueue.remove(digest);
//
//         dLog.remove(digest);
//         return true;
//      }
//      return false;
//   }

   public boolean assign(String digest, PBFTReply reply){
      if(digest == null) return false;

      for(StatedPBFTRequestMessage sr : requestLog.values()){
        if(sr.getDigest().equals(digest)){
           sr.setReply(reply);
           return true;
        }
      }

      return false;
   }

   public boolean assign(String digest, PBFTRequest request){
      if(digest == null) return false;

      for(StatedPBFTRequestMessage sr : requestLog.values()){
        if(sr.getDigest().equals(digest)){
           sr.setRequest(request);
           return true;
        }
      }

      return false;
   }


   public boolean assign(String digest, Long seqn){
      if(digest == null) return false;

      for(StatedPBFTRequestMessage loggedRequest : requestLog.values()){
        if(loggedRequest.getDigest().equals(digest)){
           loggedRequest.setSequenceNumber(seqn);
           return true;
        }
      }

      return false;
   }


   public boolean is(String digest, RequestState state){
      
      if(digest == null) return false;

      for(StatedPBFTRequestMessage sr : requestLog.values()){
        if(sr.getDigest().equals(digest)){
           return state != null && state.equals(sr.getState());
        }
      }

      return false;
   }

   public boolean is(PBFTRequest receveidRequest, RequestState state){
      if(!(receveidRequest != null && receveidRequest.getClientID() != null && receveidRequest.getTimestamp() != null)){
         return false;
      }

      for(StatedPBFTRequestMessage statedRequest : requestLog.values()){

        PBFTRequest loggedRequest = statedRequest.getRequest();

        if(loggedRequest != null && loggedRequest.getClientID() != null && loggedRequest.getTimestamp() != null){

           if(loggedRequest.getClientID().equals(receveidRequest.getClientID()) && loggedRequest.getTimestamp().equals(receveidRequest.getTimestamp())){

              return state != null && state.equals(statedRequest.getState());

           }
           
        }

      }

      return false;
   }

   public StatedPBFTRequestMessage getStatedRequest(String digest){
      if(digest == null) return null;

      for(StatedPBFTRequestMessage statedRequest : requestLog.values()){
        if(statedRequest.getDigest().equals(digest)){
           return statedRequest;
        }
      }

      return null;
   }

   public StatedPBFTRequestMessage getStatedRequest(PBFTReply reply){
      if(!(reply != null && reply.getClientID() != null && reply.getTimestamp() != null)){
         return null;
      }

      for(StatedPBFTRequestMessage statedRequest : requestLog.values()){

        PBFTRequest loggedRequest = statedRequest.getRequest();

        if(loggedRequest != null && loggedRequest.getClientID() != null && loggedRequest.getTimestamp() != null){

           if(loggedRequest.getClientID().equals(reply.getClientID()) && loggedRequest.getTimestamp().equals(reply.getTimestamp())){

              return statedRequest;

           }

        }

      }

      return null;
   }

   public StatedPBFTRequestMessage getStatedRequest(PBFTRequest receveidRequest){
      if(!(receveidRequest != null && receveidRequest.getClientID() != null && receveidRequest.getTimestamp() != null)){
         return null;
      }

      for(StatedPBFTRequestMessage statedRequest : requestLog.values()){

        PBFTRequest loggedRequest = statedRequest.getRequest();

        if(loggedRequest != null && loggedRequest.getClientID() != null && loggedRequest.getTimestamp() != null){

           if(loggedRequest.getClientID().equals(receveidRequest.getClientID()) && loggedRequest.getTimestamp().equals(receveidRequest.getTimestamp())){

              return statedRequest;

           }

        }

      }

      return null;
   }


   public boolean is(Long seqn,  RequestState state){
      if(!(seqn != null && state != null)){
         return false;
      }

      boolean is = true;
      long count = 0;

      for(StatedPBFTRequestMessage sr : requestLog.values()){
         Long seqn0 = sr.getSequenceNumber();
         if(seqn0 != null && seqn0.equals(seqn)){
            is = is && state.equals(sr.getState());
            count ++;
         }
      }
      return is && count > 0;
   }

   public boolean logged(PBFTRequest r){
      return (!(!isWaiting(r) && !wasPrePrepared(r) /*&& !wasPrepared(r) && !wasCommitted(r) && !wasServed(r)*/));
   }

   public boolean isOld(PBFTRequest r){
      if(!(r != null && r.getClientID() != null && r.getTimestamp() != null)){
         return false;
      }
      Long currentTimestamp = sessions.get(r.getClientID());

      return (currentTimestamp != null && currentTimestamp.longValue() >= r.getTimestamp().longValue());
      
   }

   public boolean isNew(PBFTRequest r){
      if(!(r != null && r.getClientID() != null && r.getTimestamp() != null)){
         return false;
      }

      return !isOld(r);
   }
   
   
   public boolean isWaiting(String digest){
      return is(digest, RequestState.WAITING);
   }

   public boolean isWaiting(PBFTRequest r){
      return is(r, RequestState.WAITING);
   }

   public boolean isWaiting(Long seqn){
      return is(seqn, RequestState.WAITING);
   }

   public boolean wasPrePrepared(String digest){
      return is(digest, RequestState.PREPREPARED) || wasPrepared(digest);
   }

   public boolean wasPrePrepared(PBFTRequest r){
      return is(r, RequestState.PREPREPARED) || wasPrepared(r);
   }

   public boolean wasPrePrepared(Long seqn){
      return is(seqn, RequestState.PREPREPARED) || wasPrepared(seqn);
   }

   public boolean hasRequest(String digest){
      return getRequest(digest) != null;
   }

   public boolean hasRequest(PBFTRequest r){
      
      if(!(r != null && r.getClientID() != null && r.getTimestamp() != null)){
         return false;
      }

      for(StatedPBFTRequestMessage sr : requestLog.values()){
         PBFTRequest r0 = sr.getRequest();

         if(r0 != null && r0.getClientID() != null && r0.getTimestamp() != null){
            if(r0.getClientID().equals(r.getClientID()) && r0.getTimestamp().equals(r.getTimestamp())){
               return true;
            }
         }
      }

      return false;
      
   }

   public boolean hasSomeRequestMissed(Long seqn){
        if(seqn == null){
           return false;
        }

        for(StatedPBFTRequestMessage sr : requestLog.values()){
           Long seqn0 = sr.getSequenceNumber();

           if(seqn0 != null && seqn0.equals(seqn)){
              if(sr.getRequest() == null){
                 return true;
              }
           }
        }

        return false;
      
   }
   public boolean wasPrepared(String digest){
      return is(digest, RequestState.PREPARED) || wasCommitted(digest);
   }

   public boolean wasPrepared(PBFTRequest r){
      return is(r, RequestState.PREPARED) || wasCommitted(r);
   }

   public boolean wasPrepared(Long seqn){
      return is(seqn, RequestState.PREPARED) || wasCommitted(seqn);
   }

  public boolean wasCommitted(String digest){
      return is(digest, RequestState.COMMITTED) || wasServed(digest);
  }

  public boolean wasCommitted(PBFTRequest r){
      return is(r, RequestState.COMMITTED) || wasServed(r);
  }

  public boolean wasCommitted(Long seqn){
      return is(seqn, RequestState.COMMITTED) || wasServed(seqn);
  }

  public boolean wasServed(String digest){
      return is(digest, RequestState.SERVED);
  }

  public boolean wasServed(PBFTRequest r){
      return is(r, RequestState.SERVED);
  }

  public boolean wasServed(Long seqn){
      return is(seqn, RequestState.SERVED);
  }

  public boolean hasSomeInState(RequestState state){
        if(state == null){
           return false;
        }

        for(StatedPBFTRequestMessage sr : requestLog.values()){
           RequestState state0 = sr.getState();

           if(state0 != null && state0.equals(state)){
              return true;
           }
        }

        return false;
   }

  public boolean hasSomeInState(Long seqn, RequestState state){
        if(state == null || seqn == null){
           return false;
        }

        for(StatedPBFTRequestMessage sr : requestLog.values()){
           RequestState state0 = sr.getState();
           Long seqn0 = sr.getSequenceNumber();

           if(seqn0 != null && seqn0.equals(seqn) && state0 != null && state0.equals(state)){
              return true;
           }
        }

        return false;
   }

   public boolean hasSomeWaiting(){
      return hasSomeInState(RequestState.WAITING);
   }

   public boolean hasSomeWaiting(Long seqn){
      return hasSomeInState(seqn, RequestState.WAITING);
   }

   public boolean hasSomePrePrepared(){
      return hasSomeInState(RequestState.PREPREPARED);
   }

   public boolean hasSomePrePrepared(Long seqn){
      return hasSomeInState(seqn, RequestState.PREPREPARED);
   }
   
   public boolean hasSomePrepared(){
      return hasSomeInState(RequestState.PREPARED);
   }

   public boolean hasSomePrepared(Long seqn){
      return hasSomeInState(seqn, RequestState.PREPARED);
   }

   public boolean hasSomeCommitted(){
      return hasSomeInState(RequestState.COMMITTED);
   }

   public boolean hasSomeCommitted(Long seqn){
      return hasSomeInState(seqn, RequestState.COMMITTED);
   }

   public boolean hasSomeServed(){
      return hasSomeInState(RequestState.SERVED);
   }

   public boolean hasSomeServed(Long seqn){
      return hasSomeInState(seqn, RequestState.SERVED);
   }

   public boolean hasSomeMissed(){
      return hasSomeInState(RequestState.MISSED);
   }

   public boolean hasSomeMissed(Long seqn){
      return hasSomeInState(seqn, RequestState.MISSED);
   }

   public DigestList getDigestsOfMissedRequests(Long seqn){
      DigestList digests = new DigestList();

      if(seqn != null){
         for(StatedPBFTRequestMessage statedRequest : requestLog.values()){
            Long seqn0 = statedRequest.getSequenceNumber();

            if(seqn0 != null && seqn0.equals(seqn) && statedRequest.getDigest() != null && statedRequest.getRequest() == null){
               digests.add(statedRequest.getDigest());
            }
         }

      }
      
      return digests;
   }

   public PBFTRequest getFirtRequestWaiting(){
      Integer findex = requestLog.firstKey();
      Integer lindex = requestLog.lastKey();

      if(findex != null && lindex != null){
         for(int index = findex; index <= lindex; index ++){
            StatedPBFTRequestMessage sr = requestLog.get(index);
            if(sr != null && sr.getRequest() != null){
               if(RequestState.WAITING.equals(sr.getState())){
                  return sr.getRequest();
               }
            }
         }
      }
      return null;
   }
   public String getFirtRequestDigestWaiting(){
      Integer findex = requestLog.firstKey();
      Integer lindex = requestLog.lastKey();

      if(findex != null && lindex != null){
         for(int index = findex; index <= lindex; index ++){
            StatedPBFTRequestMessage sr = requestLog.get(index);
            if(sr != null && sr.getRequest() != null){
               if(RequestState.WAITING.equals(sr.getState())){
                  return sr.getDigest();
               }
            }
         }
      }
      return null;
   }
   
   public int getWaitingQueueSize(){
      Integer findex = requestLog.firstKey();
      Integer lindex = requestLog.lastKey();
      int count = 0;
      if(findex != null && lindex != null){
         for(int index = findex; index <= lindex; index ++){
            StatedPBFTRequestMessage sr = requestLog.get(index);
            if(sr != null && sr.getRequest() != null){
               if(RequestState.WAITING.equals(sr.getState())){
                  count ++;
               }
            }
         }
      }
      return count;
   }

//   public int getQueueSize(){
//      return dQueue.size();
//   }
//   public int getSizeInBytes(){
//      int size = 0;
//
//      for(int i = 0; i < dQueue.size(); i++){
//         String digest = dQueue.get(i);
//         size += getRequestSize(digest);
//      }
//
//      return size;
//   }

   public int getRequestSize(String digest){
      PBFTRequest r = getRequest(digest);
      return r == null ? 0 : r.getSize();
   }

//   public String getDigestFromQueue(){
//      return dQueue.remove();
//   }
//
//   public boolean digestQueueIsEmpty(){
//      return dQueue.isEmpty();
//   }
}
