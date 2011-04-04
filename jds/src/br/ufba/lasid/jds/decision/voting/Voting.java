/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.voting;

/**
 * A voting is a decision strategy where a subject is chosen based on votes.
 * @author aliriosa
 */
public abstract class Voting implements IVoting{

   protected VoteList  votes   = new VoteList();
   protected ICounting counting;

   public Voting(ICounting counting) {
      this.counting = counting;
   }

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
    * Performs the count of the votes.
    */
   public void count(){
      counting.count(votes);
   }

   public VoteList getVotes() {
      return votes;
   }

   public ICounting getCounting() {
      return counting;
   }

}
