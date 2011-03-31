/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.Subject;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;

/**
 *
 * @author aliriosa
 */
public class CommitSubject extends Subject{

   protected PBFTCommit commit;

   public static final int SEQUENCENUMBER = 0;
   public static final int     VIEWNUMBER = 1;

   public CommitSubject(PBFTCommit commit) {
      this.commit = commit;
   }

   public boolean equals(ISubject b) {
      if(!(b != null && (b instanceof CommitSubject))){
         return false;
      }

      CommitSubject cs = (CommitSubject) b;

      try{
         return (
            cs.commit.getViewNumber().equals(commit.getViewNumber())           &&
            cs.commit.getSequenceNumber().equals(commit.getSequenceNumber())
         );

      }catch(Exception e){
         return false;
      }
   }

   public PBFTCommit getCommit(){
      return commit;
   }

   public Object getInfo(int i) {
      switch(i){
         case SEQUENCENUMBER:
            return commit.getSequenceNumber();
         case VIEWNUMBER:
            return commit.getViewNumber();
         default:
            return null;
      }
   }


}
