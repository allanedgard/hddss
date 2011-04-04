/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTNullRequest extends PBFTRequest{

   public PBFTNullRequest(Long timestamp, Object clientID) {
      super(null, timestamp, clientID);
   }
}
