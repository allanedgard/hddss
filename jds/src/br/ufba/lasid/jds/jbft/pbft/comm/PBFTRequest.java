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

    protected PBFTRequest(){
        
    }

/*    public PBFTRequest(IPayload payload, Long timestamp, Object clientID, Long lastTimestamp){
        setPayload(payload);
        setTimestamp(timestamp);
        setClientID(clientID);
        setLastTimestamp(lastTimestamp);
    }*/

    public PBFTRequest(IPayload payload, Long timestamp, Object clientID){

//        this(payload, timestamp, clientID);//, new Long(-1));
        setPayload(payload);
        setTimestamp(timestamp);
        setClientID(clientID);

    }

    public static PBFTRequest copy(PBFTRequest r){

        return new PBFTRequest(
                        r.getPayload(),
                        r.getTimestamp(),
                        r.getClientID()//,
//                        r.getLastTimestamp()
                    );
    }


    @Override
    public final synchronized String toString() {
        
//        String lasttimestamp = "null";
//
//        if(getLastTimestamp() != null){
//            lasttimestamp = getLastTimestamp().toString();
//        }
//        return (
//                "<REQUEST"                                  + ", " +
//                 "C = " + getClientID().toString()     + ", " +
//                 "(LT = " + lasttimestamp + "; " +
//                 "CT = " + getTimestamp().toString() + "), " +
//                 "OP = " + getPayload().toString()   +
//                 ">"
//        );

        return (
                "<REQUEST"                             + ", " +
                 "CLIENT = " + getClientID().toString()     + ", " +
                 "TIMESTAMP = " + getTimestamp().toString()   + ", " +
                 "OPERATION = " + getPayload().toString()   +
                 ">"
        );

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

    private boolean sent = false;

    public boolean wasSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

//    protected Long lastTimestamp;
//
//    public void setLastTimestamp(Long timestamp){
//        lastTimestamp = timestamp;//put("LASTTIMESTAMP", timestamp);
//    }

//    public Long getLastTimestamp(){
//        return lastTimestamp; //(Long)get("LASTTIMESTAMP");
//    }

    protected boolean synch = true;

    public boolean isSynch() {
        return synch;
    }

    public void setSynch(boolean synch) {
        this.synch = synch;
    }

    
}
