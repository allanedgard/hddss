/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision;

/**
 * A voting is a decision strategy where a subject is chosen based on votes.
 * @author aliriosa
 */
public abstract class Voting implements IVoting{

   protected VoteList  votes   = new VoteList();
   protected Counting counting = new Counting();

   /**
    * Adds a vote for the list of votes if it is not null and has not been added yet.
    * @param vote - the vote
    */
   public void add(IVote vote){
      if(vote != null && !votes.contains(vote)){
         votes.add(vote);
      }
   }

   /**
    * Performs the counting of the votes.
    */
   public void counting(){
      counting.clear();
      
      /* for each vote collected */
      for(int i = 0; i < votes.size(); i++){

         IVote vi = votes.get(i);
         ISubject si = vi.getSubject();
         IElector ei = vi.getElector();

         /* if the subject si is a valid subject and has not been computed yet */
         if(si != null && !counting.containsKey(si)){
            long count = 1;

            counting.put(si, count);
            
            for(int j = i; j < votes.size(); j++){
               IVote vj = votes.get(j);
               ISubject sj = vj.getSubject();
               IElector ej = vj.getElector();

               /* if the electors are differents or if the electors are anonimous then the vote'll be considered */
               if((ei != null && ej != null && !ei.equals(ej)) || (ei == null && ej == null)){

                  /* if sj is a valid subject and it is equal to si */
                  if(sj != null && sj.equals(si)){
                     count ++;
                     counting.put(si, count);
                  }
               }
            }
         }
      }
   }

   public VoteList getVotes() {
      return votes;
   }

   public Counting getCounting() {
      return counting;
   }

}
