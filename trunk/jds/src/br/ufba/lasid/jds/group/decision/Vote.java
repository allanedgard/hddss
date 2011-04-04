/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.group.decision;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.decision.voting.IElector;
import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.voting.IVote;

/**
 *
 * @author aliriosa
 */
public class Vote implements IVote{
   IElector elector;
   ISubject subject;

   public Vote(Object pid, ISubject subject){
      this(new ElectorProcess(pid), subject);
   }
   public Vote(IProcess process, ISubject subject){
      this(new ElectorProcess(process), subject);
   }
   
   public Vote(IElector elector, ISubject subject) {
      this.elector = elector;
      this.subject = subject;
   }   
   
   public ISubject getSubject() {
      return subject;
   }

   public IElector getElector() {
      return elector;
   }

   @Override
   public boolean equals(Object obj) {
      try{
         if(!(obj instanceof IVote)){
            return false;
         }

         IVote vote = (IVote)obj;

         return vote.getElector().equals(elector) && vote.getSubject().equals(subject);
         
      }catch(Exception e){
         return false;
      }
   }



}
