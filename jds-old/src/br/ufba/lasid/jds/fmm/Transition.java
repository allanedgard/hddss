/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.fmm;

import br.ufba.lasid.jds.util.JDSUtility;

/**
 *
 * @author aliriosa
 */
public class Transition {
   protected int ifrom;
   protected int ito;
   protected MultiModeMachine machine;

   public Transition(MultiModeMachine machine, int from, int to) {
      this.ifrom = from;
      this.ito = to;
      this.machine = machine;
   }

   public boolean able(){
      Mode fmode = machine.getMode(ifrom);
      Mode tmode   = machine.getMode(ito);
      if(fmode != null && tmode != null){
         return !fmode.able() && tmode.able();
      }

      return false;
   }

   public void swap(){
      if(able()){
         Mode from = machine.getMode(ifrom);
         Mode to = machine.getMode(ito);
         JDSUtility.debug("[MultiModeMachine:swap(mode)] it has switched from " + (from  == null ? "NULL" : from) + " state to " + (to == null ? "NULL" : to) + " state.");
         machine.swap(ito);
      }
   }
}
