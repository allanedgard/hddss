/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.client.decision;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.Subject;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;

/**
 *
 * @author aliriosa
 */
public class ReplySubject extends Subject{
   protected PBFTReply reply;

   public static final int    PAYLOAD = 0;
   public static final int   CLIENTID = 1;
   public static final int  TIMESTAMP = 2;
   public static final int VIEWNUMBER = 3;

   public ReplySubject(PBFTReply reply) {
      if(reply == null){
         throw new NullPointerException("Reply can not be null.");
      }
      this.reply = reply;
   }

   
   public boolean equals(ISubject b) {
      if(!(b != null && (b instanceof ReplySubject))){
         return false;
      }
      try{
         ReplySubject rs = (ReplySubject) b;

         return (
            rs.reply.getClientID().equals(reply.getClientID())       &&
            rs.reply.getTimestamp().equals(reply.getTimestamp())     &&
            rs.reply.getViewNumber().equals(reply.getViewNumber())   &&
            rs.reply.getPayload().equals(reply.getPayload())
         );
      }catch(Exception e){
         return false;
      }

   }

   public Object getInfo(int i){
      switch(i){
         case PAYLOAD:
            return reply.getPayload();
         case CLIENTID:
            return reply.getClientID();
         case TIMESTAMP:
            return reply.getTimestamp();
         case VIEWNUMBER:
            return reply.getViewNumber();
         default:
            return null;
      }
   }

}
