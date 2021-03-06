/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageCollection;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;

/**
 *
 * @author aliriosa
 */
public class PBFTBag extends PBFTServerMessage{

    protected MessageCollection messages = new MessageCollection();

    public MessageCollection getMessages() {
        return messages;
    }
    
    public void addMessage(IMessage m){
        messages.add(m);
    }

    public PBFTBag(Object replicaID){
        setReplicaID(replicaID);
    }
    
    @Override
    public final String toString() {

        return (
                "<BAG" + ", " +
                 "EXEC-SEQUENCE = " + getSequenceNumber() + ", " +
                 "SENDER = " + getReplicaID().toString() + ", " +
                 "SIZE = " + messages.size() + 
                 ">"
        );
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

   public int getTAG() {
      return IPBFTServer.BAG;
   }

   public String getTAGString() {
      return "BAG";
   }
}
