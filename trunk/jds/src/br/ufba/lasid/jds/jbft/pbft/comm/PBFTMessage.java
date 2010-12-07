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
public abstract class PBFTMessage extends ClientServerMessage{

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
    public static String SETPREPREPAREMSGS = "__SETPREPREPAREMSGS";
    public static String VIEWCHANGEMSGS = "__VIEWCHANGEMSGS";
    public static String REQUESTDONEFIELD = "__REQUESTDONEFIELD";
    public static String BATCHSTATEFIELD = "__BATCHSTATEFIELD";
    public static String EXECUTEDFIELD = "__EXECUTEDFIELD";




    public enum TYPE{

        SENDREQUEST(0),
        RECEIVEREQUEST(1),
        EXECUTE(2),
        SENDREPLY(3),
        RECEIVEREPLY(4),
        SENDPREPREPARE(5),
        RECEIVEPREPREPARE(6),
        SENDPREPARE(7),
        RECEIVEPREPARE(8),
        SENDCOMMIT(9),
        RECEIVECOMMIT(10),
        REQUESTRETRANSMITION(11),
        CHANGEVIEW(12),
        BATCHING(13),
        SENDCHECKPOINT(14),
        FETCHSTATE(15),
        EXECUTECHECKPOINT(16),
        NEWVIEW(17),
        RECEIVECHECKPOINT(18);
      
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
        PBFTMessage m = new PBFTRequestMessage();
        m.put(TYPEFIELD, TYPE.SENDREQUEST);
        return m;
    }

    public static PBFTMessage translateTo(PBFTMessage m, TYPE type){
        m.put(TYPEFIELD, type);
        return m;
    }

    public abstract String getID();
}
