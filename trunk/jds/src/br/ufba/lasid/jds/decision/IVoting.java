/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision;

/**
 *
 * @author aliriosa
 */
public interface IVoting extends IDecisionStrategy {
   public void add(IVote vote);
   public void counting();
   public VoteList getVotes();
   public Counting getCounting();
}
