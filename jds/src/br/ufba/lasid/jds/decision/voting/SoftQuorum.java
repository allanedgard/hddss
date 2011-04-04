/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.voting;

/**
 *
 * @author aliriosa
 */
public class SoftQuorum extends Quorum{

   public SoftQuorum(int minimumNumberOfVotes) {
      super(minimumNumberOfVotes, new SoftCounting());
   }
   
}
