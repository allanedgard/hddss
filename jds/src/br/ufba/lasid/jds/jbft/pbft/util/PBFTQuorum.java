/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTQuorum extends Quorum{

    public PBFTQuorum(int quorumSize) {
        super(quorumSize);
    }

    public PBFTQuorum() {
    }

    public void setNOP(){
        for(IMessage m : this){
            PBFTMessage m1 = (PBFTMessage)m;
            m1.setNop(true);            
        }
    }

    public boolean isNOP(){
        boolean nop = true;
        for(IMessage m : this){
            PBFTMessage m1 = (PBFTMessage)m;
            nop = nop && m1.isNop();
        }

        return nop;
    }

    public boolean isNOP(Object rid){

        try{
            for(IMessage m : this){

                PBFTServerMessage m1 = (PBFTServerMessage) m;

                if(m1.getReplicaID().equals(rid)){
                    return true;
                }
            }
        }catch(Exception except){
            except.printStackTrace();
        }
        return false;
    }
}
