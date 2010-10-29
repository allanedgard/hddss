/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss.bft;

import br.ufba.lasid.hddss.Message;

/**
 *
 * @author aliriosa
 */
public class BFTMessage extends Message {

    public enum TYPE{
        REQUEST, PRE_PREPARE, PREPARE, COMMIT, REPLY
    }
    
    private TYPE bfttype;

    private long size = 0;
    
    public BFTMessage(int from, int to, Object body) {
        super(from, to, body);
    }

    public void setBFTType(TYPE type){
        this.bfttype = type;
    }

    public TYPE getType(){
        return bfttype;
    }

    public static BFTMessage create(int from, int to, Object body, TYPE bfttype){
        BFTMessage m = new BFTMessage(from, to, body);
        m.bfttype = bfttype;
        return m;
    }

    public static BFTMessage newRequest(int from, int to, Object body){
        return create(from, to, body, TYPE.REQUEST);
    }

    public static BFTMessage newReplay(int from, int to, Object body){
        return create(from, to, body,  TYPE.REPLY);
    }

    public static BFTMessage newPrePrepare(int from, int to, Object body){
        return create(from, to, body,  TYPE.PRE_PREPARE);
    }

    public static BFTMessage newPrepare(int from, int to, Object body){
        return create(from, to, body,  TYPE.PREPARE);
    }

    public static BFTMessage newCommit(int from, int to, Object body){
        return create(from, to, body,  TYPE.COMMIT);
    }

    public boolean isA(TYPE type){
        return this.bfttype.equals(type);
    }

    public long getSize(){
        return size;
    }
    
    public void setSize(long s){
        size = s;
    }
}
