/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision;

/**
 *
 * @author aliriosa
 */
public abstract class Subject implements ISubject{

   @Override
   public boolean equals(Object obj) {
      try{
         return equals((ISubject)obj);
      }catch(Exception e){
         return false;
      }
   }



}
