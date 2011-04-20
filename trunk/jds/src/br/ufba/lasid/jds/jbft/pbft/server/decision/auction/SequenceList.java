/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision.auction;

import br.ufba.lasid.jds.decision.bargaining.auction.ILot;
import br.ufba.lasid.jds.decision.bargaining.auction.LotList;
import java.util.Comparator;

/**
 *
 * @author aliriosa
 */
public class SequenceList extends LotList{

   public Sequence get(long seqn){
      Sequence lookup = null;
      for(ILot lot : this){
         if(lot instanceof Sequence){
            Sequence current = (Sequence)lot;
            if(current.sequence == seqn){
               lookup = current;
               break;
            }
         }
      }
      return lookup;
   }

   public boolean contains(long seqn){
      return get(seqn) != null;
   }

   public boolean add(long seqn) {
      return super.add(new Sequence(seqn));
   }

   public static class SequenceComparator implements Comparator<ILot>{

      public int compare(ILot a1, ILot a2) {
         Sequence o1 = (Sequence) a1;
         Sequence o2 = (Sequence) a2;
         if(o1 == null && o2 == null){
            return 0;
         }
         if(o1 == null && o2 != null){
            return -1;
         }

         if(o1 != null && o2 == null){
            return 1;
         }
         return o1.compareTo(o2);
      }
      
   }
}
