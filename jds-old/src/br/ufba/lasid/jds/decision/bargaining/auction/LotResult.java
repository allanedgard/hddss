/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.bargaining.auction;

import br.ufba.lasid.jds.decision.bargaining.IProposal;
import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class LotResult implements ILotResult{
   ArrayList<LotResultItem> items = new ArrayList<LotResultItem>();

   public boolean isEmpty(){
      return items.isEmpty();
   }
   
   public int size(){
      return items.size();
   }

   public void remove(int i){
      items.remove(i);
   }

   public int indexof(IProposal proposal){
      int i = -1;
      for(int j = 0; j < size(); j++){
         LotResultItem item = items.get(j);
         if(item != null && item.proposal.compareTo(proposal) == IProposal.EQUAL){
            i = j;
            break;
         }
      }
      return i;
   }
   public void add(IProposal proposal, int count){

      int i = indexof(proposal);

      if(i >= 0){
         items.get(i).proposal = proposal;
         items.get(i).count = count;
         return;
      }
      
      LotResultItem item = new LotResultItem(proposal, count);
      items.add(item);

   }

   private LotResultItem getItem(int i){
      if(i < items.size()){
         return items.get(i);
      }

      return null;
   }
   public IProposal getProposal(int i){
      LotResultItem item = getItem(i);

      if(item != null){
         return item.proposal;
      }

      return null;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final LotResult other = (LotResult) obj;
      if (this.items != other.items && (this.items == null || !this.items.equals(other.items))) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 5;
      hash = 83 * hash + (this.items != null ? this.items.hashCode() : 0);
      return hash;
   }



   public int getCount(int i){
      LotResultItem item = getItem(i);

      if(item != null){
         return item.count;
      }

      return -1;
   }

   class LotResultItem{
      IProposal proposal;
      int count;

      public LotResultItem(IProposal proposal, int count) {
         this.proposal = proposal;
         this.count = count;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         final LotResultItem other = (LotResultItem) obj;
         
         if (this.proposal != other.proposal && (this.proposal == null || !(this.proposal.compareTo(other.proposal) == IProposal.EQUAL))) {
            return false;
         }
         if (this.count != other.count) {
            return false;
         }
         return true;
      }

      @Override
      public int hashCode() {
         int hash = 3;
         hash = 73 * hash + (this.proposal != null ? this.proposal.hashCode() : 0);
         hash = 73 * hash + this.count;
         return hash;
      }

      
   }
}
