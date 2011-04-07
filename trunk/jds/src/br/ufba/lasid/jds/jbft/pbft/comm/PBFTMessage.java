/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.comm.IMessage;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTMessage implements IMessage{
    @Override
    public abstract String toString();

    
    transient protected long sendTime;
    transient protected long recvTime;

   public long getRecvTime() {
      return recvTime;
   }

   public void setRecvTime(long recvTime) {
      this.recvTime = recvTime;
   }

   public long getSendTime() {
      return sendTime;
   }

   public void setSendTime(long sendTime) {
      this.sendTime = sendTime;
   }

    
    
}