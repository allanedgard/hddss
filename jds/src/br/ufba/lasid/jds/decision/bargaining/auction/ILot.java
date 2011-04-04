/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.bargaining.auction;

/**
 *
 * @author aliriosa
 */
public interface ILot<Item> {
   
   public void add(Item i);
   
   /* return the number of size*/
   public int size();

   public int getLotID();
   
}
