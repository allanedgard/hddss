/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTNewView extends PBFTServerMessage{
    Object digestSet;
    public void setDigestSet(Object dset){
        digestSet = dset;
        //put(AbstractPBFTMessage.ACCEPTEDDIGESTSET, dset); /*S*/
    }

    public Object getDigestSet(){
        return digestSet;
        //return get(AbstractPBFTMessage.ACCEPTEDDIGESTSET); /*S*/
    }

    Object procSet;
    public void setProcessingSet(Object dset){
        procSet = dset; //put(AbstractPBFTMessage.TOPROCESSINGINNEWVIEWSET, dset); /*X*/
    }

    public Object getProcessingSet(){
        return procSet;//get(AbstractPBFTMessage.TOPROCESSINGINNEWVIEWSET); /*X*/
    }

    @Override
    public final synchronized String toString() {

        return (
                "<NEW-VIEW"                             + "," +
                 "VIEW = " + getViewNumber().toString() + ", " +
                 "V = " + getDigestSet().toString()     + ", " +
                 "X = " + getProcessingSet().toString() + ", " +
                 "SENDER = " + getReplicaID().toString()+ 
                 ">"
        );
    }
    
}
