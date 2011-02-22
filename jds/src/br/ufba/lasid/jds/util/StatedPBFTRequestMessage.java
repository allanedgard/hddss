/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;

/**
 *
 * @author aliriosa
 */
public class StatedPBFTRequestMessage {

    public enum RequestState{
        NONE, WAITING, PREPREPARED, PREPARED, COMMITTED, SERVED
    }

    private volatile RequestState rstate = RequestState.NONE;
    private volatile String digest = null;
    private volatile PBFTRequest request = null;
    private volatile PBFTReply   reply = null;
    private volatile Long sequenceNumber = null;
    
    public StatedPBFTRequestMessage(PBFTRequest request, RequestState rstate, String digest) {
        this.request = request;
        this.rstate = rstate;
        this.digest = digest;
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
   

}
