/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTMessage extends ClientServerMessage{

    public static String VIEWFIELD = "__VIEW";
    public static String REQUESTFIELD = "__REQUEST";
    public static String DIGESTFIELD = "__DIGESTFIELD";
    public static String REPLICAIDFIELD = "__REPLICAIDFIELD";
    public static String SEQUENCENUMBERFIELD = "__SEQUENCENUMBERFIELD";
    public static String TIMESTAMPFIELD = "__TIMESTAMPFIELD";
    public static String CLIENTFIELD = "__CLIENTFIELD";
    
    public enum TYPE{

        SENDREQUEST(0),
        RECEIVEREQUEST(1),
        EXECUTE(2),
        SENDREPLY(3),
        RECEIVEREPLY(4),
        PREPREPARE(5),
        PREPARE(6),
        COMMIT(7),
        REQUESTRETRANSMITION(8);
      
        private final int value;

        TYPE(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }

    }

    protected static long SEQ = 0;
    protected long mySEQ = 0;

    public static long newSequenceNumber(){
        return ++SEQ;
    }

    public static PBFTMessage newRequest(){
        PBFTMessage m = new PBFTMessage();
        m.put(TYPEFIELD, TYPE.SENDREQUEST);
        return m;
    }

    public static PBFTMessage translateTo(PBFTMessage m, TYPE type){
        m.put(TYPEFIELD, type);
        return m;
    }
}
