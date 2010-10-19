/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class ConteudoTimedCB {
    
    int LCB;
    String mensagem;
    int [] ultimaMsgTimeStamp;
    Acknowledge [] vack;

    ConteudoTimedCB(String m, int l, Acknowledge [] acks) {
        LCB = l;
        mensagem = m;
        vack = acks;
    }

    ConteudoTimedCB(String m, int l, int [] u) {
        LCB = l;
        mensagem = m;
        ultimaMsgTimeStamp = u;
    }

    ConteudoTimedCB(String m, int l) {
        LCB = l;
        mensagem = m;
    }
    
    @Override  
    public String toString() {
        return mensagem;
    }

}
