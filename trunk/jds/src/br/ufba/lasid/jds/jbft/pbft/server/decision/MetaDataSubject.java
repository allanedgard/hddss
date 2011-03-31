/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.Subject;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMetaData;

/**
 *
 * @author aliriosa
 */
public class MetaDataSubject extends Subject{

   protected PBFTMetaData metadata;

   public static final int CHECKPOINT = 0;
   public static final int   SUBPARTS = 1;
   public static final int  REPLICAID = 2;

   public MetaDataSubject(PBFTMetaData metadata) {
      this.metadata = metadata;
   }

   public boolean equals(ISubject b) {
      if(!(b != null && (b instanceof MetaDataSubject))){
         return false;
      }

      MetaDataSubject cs = (MetaDataSubject) b;

      try{
         return (
            cs.metadata.getCheckpoint() == metadata.getCheckpoint() &&
            cs.metadata.getSubparts().equals(metadata.getSubparts())
         );

      }catch(Exception e){
         return false;
      }
   }

   public Object getInfo(int i) {
      switch(i){
         case CHECKPOINT:
            return metadata.getCheckpoint();
         case SUBPARTS:
            return metadata.getSubparts();
         case REPLICAID:
            return metadata.getReplicaID();
         default:
            return null;
      }
   }


}
