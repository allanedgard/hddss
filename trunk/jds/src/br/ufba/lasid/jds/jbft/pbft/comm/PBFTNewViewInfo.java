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
public class PBFTNewViewInfo {

   NewViewtable table = new NewViewtable();

   public boolean gc(int view){
      boolean ok = false;
      if(!table.isEmpty()){
         int fView = table.firstKey();
         int lView = table.lastKey();

         for(int lookup = lView; lookup >= fView; lookup --){
            table.remove(lookup);
            ok = true;
         }

      }

      return ok;
   }
   
   public boolean put(PBFTNewView nv){
      if(isValid(nv)){
         if(!table.containsKey(nv.getViewNumber())){
            table.put(nv.getViewNumber(), nv);
            return true;
         }
      }
      return false;
   }

   public PBFTNewView get(int view){
      if(!table.isEmpty()){
         if(table.containsKey(view)){
            return table.get(view);
         }

      }
      return null;
   }

   public boolean isValid(PBFTNewView nv){
      if(!(nv!= null && nv.getViewNumber()!= null && nv.getSequenceNumber()!= null && nv.getDigest()!= null && nv.getChangeViewtable()!= null && nv.getPrePrepareSet()!= null)){
         return false;
      }
      
      return true;
   }

   class NewViewtable extends TreeMap<Integer, PBFTNewView>{
      
   }
}
