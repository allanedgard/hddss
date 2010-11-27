/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft;

import br.ufba.lasid.jbft.Message;
import java.util.StringTokenizer;

/**
 *
 * @author aliriosa
 */
public class PBFTMessage extends Message{
    String body = "";
    String more = "";
    
    public void add(Object value) {
        body += more + value.toString();

        if(more.equals("")){
            more = "|";
        }
        
    }

    public Object get(int i) {
        return body.split("|")[i];
    }
    
    public enum TYPE{

        SENDREQUEST(0),
        RECEIVEREQUEST(1),
        PREPREPARE(2),
        PREPARE(3),
        COMMIT(4),
        SENDREPLY(5),
        EXECUTE(6),
        RECEIVEREPLY(7);
        
        private final int value;

        TYPE(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }

    public PBFTMessage(TYPE type){
        setType(type);
    }

    public void setType(TYPE type){
        setType(type.getValue());
    }

    @Override
    public String toString() {
        return body;
    }


}
