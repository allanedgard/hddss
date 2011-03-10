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
public class PBFTReply extends PBFTServerMessage{

    public PBFTReply(PBFTRequest req, IPayload result, Object replicaID, Integer viewNumber){
        setClientID(req.getClientID());
        setTimestamp(req.getTimestamp());
        setPayload(result);
        setReplicaID(replicaID);
        setViewNumber(viewNumber);
    }
    
    protected Object clientID;

    public Object getClientID(){
        return clientID;
    }

    public void setClientID(Object cid){
        this.clientID = cid;
    }
    
    protected Long timestamp;
    
    public Long getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(Long timestamp){
        this.timestamp = timestamp;
    }

    protected IPayload payload;

    public IPayload getPayload() {
        return payload;
    }

    public void setPayload(IPayload payload) {
        this.payload = payload;
    }

    @Override
    public final String toString() {

        return (
                "<REPLY" + ", " +
                 "VIEW = " + getViewNumber().toString()     + ", " +
                 "TIMESTAMP = " + getTimestamp().toString() + ", " +
                 "CLIENT = " + getClientID().toString()     + ", " +
                 "SENDER = " + getReplicaID().toString()    + ", " +
                 "RESULT = " + getPayload().toString()      +
                 ">"
        );
    }

    public boolean isSameRound(PBFTReply r){
        if(
            r != null                 &&
            r.getClientID()  != null  &&
            r.getTimestamp()  != null &&
            r.getViewNumber() != null
        ){
            if( r.getClientID().equals(getClientID())   &&
                r.getTimestamp().equals(getTimestamp()) &&
                r.getViewNumber().equals(getViewNumber())
             ){
                return true;
            }
        }
        
        return false;

    }

    public boolean isSameServer(PBFTReply r){
        if(r != null && r.getReplicaID() != null){
            return r.getReplicaID().equals(getReplicaID());
        }
        
        return false;
    }
}
