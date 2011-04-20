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
public class PBFTCommitInfo {
   CommitDatabase database = new CommitDatabase();

   public boolean isValid(PBFTCommit p){

      if(!(p != null && p.getSequenceNumber() != null && p.getViewNumber() != null && p.getReplicaID() != null)){
         return false;
      }

      return true;
   }

   public boolean gc(int view){
      boolean ok = false;

      if(!database.isEmpty()){
         int fView = database.firstKey();
         int lView = database.lastKey();

         for(int lookup = fView; lookup <= lView; lookup ++){
            if(lookup <= view){
               database.remove(lookup);
               ok = true;
            }
         }
      }

      return ok;
   }

   public boolean gc(int view, long seqn){

      boolean ok = false;

      if(!database.isEmpty() && database.containsKey(view)){
         Committable table = database.get(view);
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

      if(!database.isEmpty()){
         int fView = database.firstKey();
         int lView = database.lastKey();

         for(int lookup = fView; lookup <= lView; lookup ++){
            ok = ok || gc(lookup, seqn);
         }
      }

      return ok;

   }

   public boolean contains(PBFTCommit p){
      if(isValid(p)){

         if(database.isEmpty()){
            return false;
         }

         int view = p.getViewNumber();

         if(!database.containsKey(view)){
            return false;
         }

         Committable table = database.get(view);

         if(table.isEmpty()){
            return false;
         }

         long seqn = p.getSequenceNumber();

         if(!table.containsKey(seqn)){
            return false;
         }

         CommitEntry entry = table.get(seqn);

         if(entry.isEmpty()){
            return false;
         }

         Object rpid = p.getReplicaID();

         if(entry.containsKey(rpid)){
            return true;
         }

      }

      return false;
   }

   public boolean put(PBFTCommit p){
      if(!isValid(p)){
         return false;
      }

      if(contains(p)){
         return false;
      }

      int view = p.getViewNumber();

      Committable table = database.get(view);

      if(table == null){
         table = new Committable();
         database.put(view, table);
      }

      long seqn = p.getSequenceNumber();

      CommitEntry entry = table.get(seqn);

      if(entry == null){
         entry = new CommitEntry();
         table.put(seqn, entry);
      }

      Object rpid = p.getReplicaID();

      entry.put(rpid, p);

      return true;
   }
   public int count(PBFTCommit c){
      int count = 0;
      if(isValid(c) && !database.isEmpty()){
         int view = c.getViewNumber();
         if(database.containsKey(view)){

            Committable table = database.get(view);

            if(!table.isEmpty()){
               long seqn = c.getSequenceNumber();
               if(table.containsKey(seqn)){
                  CommitEntry entry = table.get(seqn);
                  return entry.size();
               }
            }
         }
      }

      return count;

   }

   public int count(int view, long seqn){
      int count = 0;
      if(!database.isEmpty()){
         if(database.containsKey(view)){

            Committable table = database.get(view);

            if(!table.isEmpty()){
               if(table.containsKey(seqn)){
                  CommitEntry entry = table.get(seqn);
                  count = entry.size();
               }
            }
         }
      }

      return count;

   }

   public PBFTCommit get(int view, long seqn, Object pid){
      if(!database.isEmpty() && database.containsKey(view)){
         Committable table = database.get(view);

         if(!table.isEmpty() && table.containsKey(seqn)){
            CommitEntry entry = table.get(seqn);
            if(!entry.isEmpty() && entry.containsKey(pid)){
               return entry.get(pid);
            }
         }
      }
      return null;
   }

   public PBFTCommit get(long seqn, Object pid){
      if(!database.isEmpty()){
         int fView = database.firstKey();
         int lView = database.lastKey();

         for(int view = lView; view >= fView; view--){
            Committable table = database.get(view);
            if(table != null && !table.isEmpty()){
               if(table.containsKey(seqn)){
                  CommitEntry entry = table.get(seqn);
                  if(entry != null && !entry.isEmpty()){
                     if(entry.containsKey(pid)){
                        return entry.get(pid);
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   public Long getFirstSequenceNumber(int view){
      if(!database.isEmpty()){
         if(database.containsKey(view)){
            Committable table = database.get(view);
            if(!table.isEmpty()){
               return table.firstKey();
            }
         }
      }
      return null;
   }

   public Long getLastSequenceNumber(int view){
      if(!database.isEmpty()){
         if(database.containsKey(view)){
            Committable table = database.get(view);
            if(!table.isEmpty()){
               return table.lastKey();
            }
         }
      }
      return null;
   }


   class CommitDatabase extends TreeMap<Integer, Committable>{
   }

   class Committable extends TreeMap<Long, CommitEntry>{
   }

   class CommitEntry extends TreeMap<Object, PBFTCommit>{
   }

}