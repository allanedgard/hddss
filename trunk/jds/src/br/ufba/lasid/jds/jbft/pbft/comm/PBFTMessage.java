/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

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
    public static String BATCHSIZEFIELD = "__BATCHSIZEFIELD";
    public static String CHECKPOINTNUMBER = "__CHECKPOINTNUMBER";
    public static String CHECKPOINTMSGS = "__CHECKPOINTMSGS";
    public static String NSREQUESTS = "__NSREQUESTS";



    public enum TYPE{

        SENDREQUEST(0),
        RECEIVEREQUEST(1),
        EXECUTE(2),
        SENDREPLY(3),
        RECEIVEREPLY(4),
        PREPREPARE(5),
        PREPARE(6),
        COMMIT(7),
        REQUESTRETRANSMITION(8),
        CHANGEVIEW(9),
        BATCHING(10),
        SENDCHECKPOINTREQUEST(11),
        FETCHSTATE(12),
        EXECUTECHECKPOINT(13);
      
        private final int value;

        TYPE(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }

    }

    protected long mySEQ = 0;
    
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