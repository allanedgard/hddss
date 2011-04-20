/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision.auction;

import br.ufba.lasid.jds.decision.bargaining.auction.ILot;

/**
 *
 * @author aliriosa
 */
public class Sequence implements ILot<Long>, Comparable<Sequence>{
   long sequence;

   public Sequence(long sequence) {
      this.sequence = sequence;
   }
   
   public void add(Long i) {
      sequence = i;
   }

   public int size() {
      return 1;
   }

   public int getLotID() {
      return ((Long)sequence).hashCode();
   }

   public long getValue(){
      return sequence;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Sequence other = (Sequence) obj;
      if (this.sequence != other.sequence) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      return ((Long)this.sequence).hashCode();
   }

   public int compareTo(Sequence o) {
      try{
         if(sequence == o.sequence){
            return 0;
         }
         if(sequence > o.sequence){
            return 1;
         }

         return -1;

      }catch(Exception e){
         return 1;
      }
   }
}
