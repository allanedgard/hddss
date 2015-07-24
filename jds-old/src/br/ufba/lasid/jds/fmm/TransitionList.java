/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.fmm;

import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class TransitionList extends ArrayList<Transition>{

   public boolean contains(int from, int to){

      for(Transition t : this){
         if(t.ifrom == from && t.ito == to){
            return true;
         }
      }
      
      return false;
   }

}
