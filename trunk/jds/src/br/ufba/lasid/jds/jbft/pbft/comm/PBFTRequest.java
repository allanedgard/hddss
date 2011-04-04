/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class PBFTRequest  extends PBFTMessage{

//    protected PBFTRequest(){
//
//    }

    public PBFTRequest(IPayload payload, Long timestamp, Object clientID){

        setPayload(payload);
        setTimestamp(timestamp);
        setClientID(clientID);

    }

    @Override
    public final String toString() {
        
      return (
         "<REQUEST, " +
            "CLIENT = " + getClientID().toString() + ", " +
            "TIMESTAMP = " + getTimestamp().toString() + ", " +
            "OPERATION = " + getPayload().toString() + ", " +
            "READ-ONLY = " + isReadOnly() + ", " +
            "SIZE = " + getSize() +
         ">"
      );
      
    }
    
    protected boolean readOnly = false;

    public void setReadOnly(boolean readOnly){
       this.readOnly = readOnly;
    }

    public boolean isReadOnly(){
       return this.readOnly;
    }

    /* mensuared in KB */
    protected int size = 1;

    public void setSize(int newSize){
       this.size = newSize;
    }

    public int getSize(){
       return this.size;
    }
    
    protected IPayload payload;

    public void setPayload(IPayload payload) {
        this.payload = payload;
    }

    public IPayload getPayload(){
        return this.payload;
    }

    protected Object clientID;

    public Object getClientID() {
        return this.clientID;
    }

    public void setClientID(Object clientID) {
        this.clientID = clientID;
    }


    protected Long timestamp;
    
    public Long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp  = timestamp;
    }

//    private boolean sent = false;
//
//    public boolean wasSent() {
//        return sent;
//    }
//
//    public void setSent(boolean sent) {
//        this.sent = sent;
//    }

    protected boolean synch = true;

    public boolean isSynch() {
        return synch;
    }

    public void setSynch(boolean synch) {
        this.synch = synch;
    }
    
}
