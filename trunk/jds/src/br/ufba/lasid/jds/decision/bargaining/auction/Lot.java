/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.bargaining.auction;

import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class Lot<Item> implements ILot<Item>{
   ArrayList<Item> items = new ArrayList<Item>();
   public void add(Item i) {
      items.add(i);
   }

   public int getLotID() {
      return this.hashCode();
   }

   public int size() {
      return items.size();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Lot<Item> other = (Lot<Item>) obj;
      if (this.items != other.items && (this.items == null || !this.items.equals(other.items))) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 79 * hash + (this.items != null ? this.items.hashCode() : 0);
      return hash;
   }

}
