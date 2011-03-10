/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class PBFTClientSessionTable extends Hashtable<Object, PBFTRequest>{

    public boolean isNext(PBFTRequest current){

        synchronized(this){
            if(current != null && current.getClientID() != null && current.getTimestamp() != null){

                PBFTRequest last = (PBFTRequest)get(current.getClientID());

                if(last != null && last.getClientID() != null && last.getTimestamp() != null){
                    return current.getTimestamp().compareTo(last.getTimestamp()) > 0;
                }

                return (last == null);
            }
        }
        return false;
        
    }

    public boolean updateClientSession(PBFTRequest r){
        synchronized(this){
            if(r != null && r.getClientID() != null && r.getTimestamp() != null){
                put(r.getClientID(), r);
                return true;
            }
        }
        return false;
    }

}
