/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import java.util.TreeMap;

/**
 *
 * @author aliriosa
 */
public class PBFTPrePrepareInfo{
   PrePrepareDatabase db = new PrePrepareDatabase();
   
   public boolean isValid(PBFTPrePrepare pp){

      if(!(pp != null && pp.getSequenceNumber() != null && pp.getViewNumber() != null && pp.getDigests() != null && pp.getReplicaID() != null)){
         return false;
      }
      
      return true;
   }

   public boolean gc(int view){
      boolean ok = false;

      if(!db.isEmpty()){
         int fView = db.firstKey();
         int lView = db.lastKey();

         for(int lookup = fView; lookup <= lView; lookup ++){
            if(lookup <= view){
               db.remove(lookup);
               ok = true;
            }
         }         
      }

      return ok;
   }

   public boolean gc(int view, long seqn){

      boolean ok = false;
      
      if(!db.isEmpty() && db.containsKey(view)){
         PrePreparetable table = db.get(view);
         if(!table.isEmpty()){
            long fSeqn = table.firstKey();
            long lSeqn = table.lastKey();

            for(long lookup = fSeqn; lookup <= lSeqn; lookup++){
               if(lookup <= seqn){
                  table.remove(lookup);
                  ok = true;
               }
            }
         }
      }

      return ok;
   }

   public boolean gc(long seqn){
      boolean ok = false;
      
      if(!db.isEmpty()){
         int fView = db.firstKey();
         int lView = db.lastKey();

         for(int lookup = fView; lookup <= lView; lookup ++){
            ok = ok || gc(lookup, seqn);
         }
      }
      
      return ok;
      
   }
   
   public boolean contains(PBFTPrePrepare pp){
      if(isValid(pp)){
         
         if(db.isEmpty()){
            return false;
         }

         int view = pp.getViewNumber();

         if(!db.containsKey(view)){
            return false;
         }

         PrePreparetable table = db.get(view);

         if(table.isEmpty()){
            return false;
         }

         long seqn = pp.getSequenceNumber();
         
         if(!table.containsKey(seqn)){
            return false;
         }

         PrePrepareEntry entry = table.get(seqn);

         if(entry.isEmpty()){
            return false;
         }

         Object rpid = pp.getReplicaID();
         
         if(entry.containsKey(rpid)){
            return true;
         }
         
      }
      
      return false;
   }
   
   public boolean put(PBFTPrePrepare pp){
      if(!isValid(pp)){
         return false;
      }

      if(contains(pp)){
         return false;
      }

      int view = pp.getViewNumber();
      
      PrePreparetable table = db.get(view);

      if(table == null){
         table = new PrePreparetable();
         db.put(view, table);
      }

      long seqn = pp.getSequenceNumber();

      PrePrepareEntry entry = table.get(seqn);

      if(entry == null){
         entry = new PrePrepareEntry();
         table.put(seqn, entry);
      }

      Object rpid = pp.getReplicaID();

      entry.put(rpid, pp);
      
      return true;
   }

   public int count(int view, long seqn){
      int count = 0;
      if(!db.isEmpty()){
         if(db.containsKey(view)){

            PrePreparetable table = db.get(view);

            if(!table.isEmpty()){
               if(table.containsKey(seqn)){
                  PrePrepareEntry entry = table.get(seqn);
                  count = entry.size();
               }
            }
         }
      }

      return count;
      
   }
   
   public PBFTPrePrepare get(long seqn){
      if(!db.isEmpty()){
         int fView = db.firstKey();
         int lView = db.lastKey();

         for(int view = lView; view >= fView; view--){
            PrePreparetable table = db.get(view);
            if(table != null && !table.isEmpty()){
               if(table.containsKey(seqn)){
                  PrePrepareEntry entry = table.get(seqn);
                  if(entry != null && !entry.isEmpty()){
                     for(Object pid : entry.keySet()){
                        return entry.get(pid);
                     }
                  }
               }
            }
         }
      }

      return null;
   }
   public PBFTPrePrepare get(int view, Object pid, String digest){
      if(!db.isEmpty() && db.containsKey(view)){
         PrePreparetable table = db.get(view);

         if(!table.isEmpty()){
            long fSeqn = table.firstKey();
            long lSeqn = table.lastKey();

            for(long lookup = fSeqn; lookup <= lSeqn; lookup ++){
               PrePrepareEntry entry = table.get(lookup);
               
               if(entry != null && !entry.isEmpty() && entry.containsKey(pid)){
                  PBFTPrePrepare pp = entry.get(pid);
                  for(String loggedDigest : pp.getDigests()){
                     if(loggedDigest.equals(digest)){
                        return pp;
                     }
                  }
               }
            }
         }
      }
      return null;
   }
   
   public PBFTPrePrepare get(int view, long seqn){
      if(!db.isEmpty() && db.containsKey(view)){
         PrePreparetable table = db.get(view);

         if(!table.isEmpty() && table.containsKey(seqn)){
            PrePrepareEntry entry = table.get(seqn);
            if(entry != null){
               return entry.firstEntry().getValue();
            }
         }
      }
      return null;
   }

   public PBFTPrePrepare get(int view, long seqn, Object pid){
      if(!db.isEmpty() && db.containsKey(view)){
         PrePreparetable table = db.get(view);

         if(!table.isEmpty() && table.containsKey(seqn)){
            PrePrepareEntry entry = table.get(seqn);
            if(!entry.isEmpty() && entry.containsKey(pid)){
               return entry.get(pid);
            }
         }
      }
      return null;
   }
   public boolean rem(PBFTPrePrepare pp){
      if(!isValid(pp)){
         return false;
      }
      return rem(pp.getViewNumber(), pp.getSequenceNumber(), pp.getReplicaID());
   }

   public boolean rem(int view, long seqn, Object pid){
      boolean ok = false;
      if(!db.isEmpty() && db.containsKey(view)){
         PrePreparetable tb = db.get(view);

         if(!tb.isEmpty() && tb.containsKey(seqn)){
            PrePrepareEntry entry = tb.get(seqn);
            if(!entry.isEmpty() && entry.containsKey(pid)){
               entry.remove(pid);
               if(entry.isEmpty()) tb.remove(seqn);
               if(tb.isEmpty()) db.remove(view);
               ok = true;
            }
         }
      }
      return ok;
   }

   public boolean isEmpty(){
      return db.isEmpty();
   }

   public boolean contains(long seqn){
      if(!db.isEmpty()){
         int fView = db.firstKey();
         int lView = db.lastKey();

         for(int view = lView; view >= fView; view --){
            if(db.containsKey(view)){
               PrePreparetable table = db.get(view);
               if(table != null && !table.isEmpty()){
                  if(table.containsKey(seqn)){
                     return true;
                  }
               }
            }
         }
      }
      return false;
   }
   class PrePrepareDatabase extends TreeMap<Integer, PrePreparetable>{
      
   }

   class PrePreparetable extends TreeMap<Long, PrePrepareEntry>{
      
   }

   class PrePrepareEntry extends TreeMap<Object, PBFTPrePrepare>{
      
   }

}

