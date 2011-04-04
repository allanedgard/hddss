/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.voting;

import br.ufba.lasid.jds.decision.ISubject;
import java.util.Hashtable;
import java.util.Set;

/**
 *
 * @author aliriosa
 */
public class SoftCounting extends Hashtable<ISubject, Long> implements ICounting{

   public Long get(ISubject subject) {
      return super.get(subject);
   }

   public Set<ISubject> getSubjects(){
      return keySet();
   }

   /**
    * Performs the count of the votes.
    * @param votes - the list of votes.
    */
   public void count(VoteList votes){
      clear();

      /* for each vote collected */
      for(int i = 0; i < votes.size(); i++){

         IVote vi = votes.get(i);
         ISubject si = vi.getSubject();
         IElector ei = vi.getElector();

         /* if the subject si is a valid subject and has not been computed yet */
         if(si != null && !containsKey(si)){
            long count = 1;

            put(si, count);

            for(int j = i; j < votes.size(); j++){
               IVote vj = votes.get(j);
               ISubject sj = vj.getSubject();
               IElector ej = vj.getElector();

               /* if the electors are differents or if the electors are anonimous then the vote'll be considered */
               if((ei != null && ej != null && !ei.equals(ej)) || (ei == null && ej == null)){

                  /* if sj is a valid subject and it is equal to si */
                  if(sj != null && sj.equals(si)){ /*canditato.match(votadoi)*/
                     count ++;
                     put(si, count);
                  }
               }
            }
         }
      }
   }

}
