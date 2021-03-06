/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class StatedPBFTRequestMessage extends PBFTMessage{

   public int getTAG() {
      return -1;
   }

   public String getTAGString() {
      return "StatedPBFTRequest";
   }

    public enum RequestState{
        NONE, WAITING, PREPREPARED, PREPARED, COMMITTED, SERVED, MISSED
    }

    

    private  RequestState rstate = RequestState.NONE;
    private  String digest = null;
    private  PBFTRequest request = null;
    private  PBFTReply   reply = null;
    private  Long sequenceNumber = null;
    private  Long requestReceiveTime = null;
    private  Long replySendTime = null;

    public StatedPBFTRequestMessage(PBFTRequest request, RequestState rstate, String digest) {
        this.request = request;
        this.rstate = rstate;
        this.digest = digest;
    }

   public Long getReplySendTime() {
      return replySendTime;
   }

   public void setReplySendTime(Long replySendTime) {
      this.replySendTime = replySendTime;
   }

   public Long getRequestReceiveTime() {
      return requestReceiveTime;
   }

   public void setRequestReceiveTime(Long requestReceiveTime) {
      this.requestReceiveTime = requestReceiveTime;
   }


    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    
    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    
    public PBFTRequest getRequest() {
        return request;
    }

    public void setRequest(PBFTRequest request) {
        this.request = request;
    }

    public PBFTReply getReply() {
        return reply;
    }

    public void setReply(PBFTReply reply) {
        this.reply = reply;
    }

    public RequestState getRstate() {
        return rstate;
    }

    public void setRstate(RequestState rstate) {
        this.rstate = rstate;
    }
    
    public RequestState getState() {
        return rstate;
    }

    public void setState(RequestState rstate) {
        this.rstate = rstate;
    }

    @Override
    public String toString() {
        return "STATED(" + this.sequenceNumber + ";" + this.rstate + ";" + this.request + "; " + this.digest + ";" + this.reply + ")";
    }
   


}
