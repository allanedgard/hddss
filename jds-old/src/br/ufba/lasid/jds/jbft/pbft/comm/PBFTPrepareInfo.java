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
public class PBFTPrepareInfo{
   PrepareDatabase database = new PrepareDatabase();

   public boolean isValid(PBFTPrepare p){

      if(!(p != null && p.getSequenceNumber() != null && p.getViewNumber() != null && p.getDigests() != null && p.getReplicaID() != null)){
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
         Preparetable table = database.get(view);
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

   public boolean contains(PBFTPrepare p){
      if(isValid(p)){

         if(database.isEmpty()){
            return false;
         }

         int view = p.getViewNumber();

         if(!database.containsKey(view)){
            return false;
         }

         Preparetable table = database.get(view);

         if(table.isEmpty()){
            return false;
         }

         long seqn = p.getSequenceNumber();

         if(!table.containsKey(seqn)){
            return false;
         }

         PrepareEntry entry = table.get(seqn);

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

   public boolean put(PBFTPrepare p){
      if(!isValid(p)){
         return false;
      }

      if(contains(p)){
         return false;
      }

      int view = p.getViewNumber();

      Preparetable table = database.get(view);

      if(table == null){
         table = new Preparetable();
         database.put(view, table);
      }

      long seqn = p.getSequenceNumber();

      PrepareEntry entry = table.get(seqn);

      if(entry == null){
         entry = new PrepareEntry();
         table.put(seqn, entry);
      }

      Object rpid = p.getReplicaID();

      entry.put(rpid, p);

      return true;
   }

   public int count(PBFTPrepare p){
      int count = 0;
      if(isValid(p) && !database.isEmpty()){
         int view = p.getViewNumber();
         if(database.containsKey(view)){
            Preparetable table = database.get(view);
            if(!table.isEmpty()){
               long seqn = p.getSequenceNumber();
               if(table.containsKey(seqn)){
                  PrepareEntry entry = table.get(seqn);
                  for(Object rpid : entry.keySet()){
                     PBFTPrepare me = entry.get(rpid);
                     if(me.getDigests().equals(p.getDigests())){
                        count ++;
                     }
                  }
               }
            }
         }
      }


      return count;
   }
   public int count(int view, long seqn){
      int max = 0;
      if(!database.isEmpty()){
         if(database.containsKey(view)){

            Preparetable table = database.get(view);

            if(!table.isEmpty()){
               if(table.containsKey(seqn)){
                  PrepareEntry entry = table.get(seqn);
                  for(Object pid : entry.keySet()){
                     int count = count(entry.get(pid));
                     if(count > max){
                        max = count;
                     }
                  }
               }
            }
         }
      }

      return max;

   }
   public PBFTPrepare get(Object pid, long seqn){
      if(!database.isEmpty()){
         int fView = database.firstKey();
         int lView = database.lastKey();

         for(int view = lView; view >= fView; view--){
            Preparetable table = database.get(view);
            if(table != null && !table.isEmpty()){
               if(table.containsKey(seqn)){
                  PrepareEntry entry = table.get(seqn);
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

   public PBFTPrepare get(int view, long seqn, Object pid){
      if(!database.isEmpty() && database.containsKey(view)){
         Preparetable table = database.get(view);

         if(!table.isEmpty() && table.containsKey(seqn)){
            PrepareEntry entry = table.get(seqn);
            if(!entry.isEmpty() && entry.containsKey(pid)){
               return entry.get(pid);
            }
         }
      }
      return null;
   }

   public boolean isEmpty(){
      return database.isEmpty();
   }
   
   public boolean contains(long seqn){
      if(!database.isEmpty()){
         int fView = database.firstKey();
         int lView = database.lastKey();

         for(int view = lView; view >= fView; view --){
            if(database.containsKey(view)){
               Preparetable table = database.get(view);
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
   
   public Long getFirstSequenceNumber(int view){
      if(!database.isEmpty()){
         if(database.containsKey(view)){
            Preparetable table = database.get(view);
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
            Preparetable table = database.get(view);
            if(!table.isEmpty()){
               return table.lastKey();
            }
         }
      }
      return null;
   }


   class PrepareDatabase extends TreeMap<Integer, Preparetable>{
   }

   class Preparetable extends TreeMap<Long, PrepareEntry>{
   }

   class PrepareEntry extends TreeMap<Object, PBFTPrepare>{
   }

}


        
        
