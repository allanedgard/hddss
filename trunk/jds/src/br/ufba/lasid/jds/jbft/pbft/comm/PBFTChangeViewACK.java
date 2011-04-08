/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeViewACK extends PBFTServerMessage{
    Object prompterID;

    public PBFTChangeViewACK(int viewn, Object replicaID, Object prompterID, String digest) {
        setViewNumber(viewn);
        setReplicaID(replicaID);
        setPrompterID(prompterID);
        setDigest(digest);
    }

    
    public Object getPrompterID() {
        return prompterID;
    }

    public void setPrompterID(Object prompterID) {
        this.prompterID = prompterID;
    }


    @Override
    public final String toString() {
        Object rid = getReplicaID();
        Object pid = getPrompterID();
        return (
                "<VIEW-CHANGE-ACK" + ", " +
                 "VIEW = " + getViewNumber() + ", " +
                 "SENDER = " + (rid == null ? "NULL" : rid) + ", " +
                 "PROMPTER = " + (pid == null ? "NULL" : pid) + ", " +
                 "DIG = " + getDigest() +
                 ">"
        );
    }

    String digest;

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

   public int getTAG() {
      return IPBFTServer.CHANGEVIEWACK;
   }

   public String getTAGString() {
      return "CHANGEVIEWACK";
   }
    


}
