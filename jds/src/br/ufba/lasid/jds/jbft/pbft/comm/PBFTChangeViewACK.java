/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeViewACK extends PBFTServerMessage{

    @Override
    public final synchronized String toString() {

        return (
                "<VIEW-CHANGE-ACK"                              + "," +
                 "VIEW = " + getViewNumber().toString()         + ", " +
                 "SENDER = " + getReplicaID().toString()        + ", " +
                 "DESTINATION = " + getReplicaID().toString()   + ", " +
                 "DIG = " + getDigest().toString()              +
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
    


}
