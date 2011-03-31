/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.group.decision;

import br.ufba.lasid.jds.BaseProcess;
import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.decision.IElector;

/**
 *
 * @author aliriosa
 */
public class ElectorProcess implements IElector{

   protected IProcess process;


   public ElectorProcess(Object pid){
      this(new BaseProcess(pid));
   }

   public ElectorProcess(IProcess process) {
      this.process = process;
   }

   public Object getID() {
      return process.getID();
   }

   @Override
   public boolean equals(Object obj){
      
      if(obj instanceof IElector)
         return this.equals((IElector)obj);

      if(obj instanceof IProcess)
         return this.equals((IProcess)obj);

      return false;
   }
   
   public boolean equals(IElector obj) {
      if (obj == null) {
         return false;
      }

      return (obj.getID() != null && obj.getID().equals(process.getID()));

   }

   public boolean equals(IProcess obj) {
      if (obj == null) {
         return false;
      }

      return (obj.getID() != null && obj.getID().equals(process.getID()));

   }

   
}
