/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision;

/**
 * A Quorum is a decision strategy based on voting which a subject is decided according
 * to a minimum number of valid votes.
 * @author aliriosa
 */
public class Quorum extends Voting{

   int nvotes = 0;
   ISubject currentDecision = null;
   
   public Quorum(int minimumNumberOfVotes) {
      this.nvotes = minimumNumberOfVotes;
   }   

   public ISubject decide(){
      
      counting();

      for(ISubject subject : counting.keySet()){
         long count = counting.get(subject);
         if(count >= nvotes){
            currentDecision = subject;
            return subject;
         }
      }
      return null;
   }

   public ISubject getCurrentDecision() {
      return currentDecision;
   }   

}
