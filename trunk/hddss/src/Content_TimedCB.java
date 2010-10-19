/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class Content_TimedCB {
    
    int LCB;
    String mensagem;
    int [] ultimaMsgTimeStamp;
    Acknowledge [] vack;

    Content_TimedCB(String m, int l, Acknowledge [] acks) {
        LCB = l;
        mensagem = m;
        vack = acks;
    }

    Content_TimedCB(String m, int l, int [] u) {
        LCB = l;
        mensagem = m;
        ultimaMsgTimeStamp = u;
    }

    Content_TimedCB(String m, int l) {
        LCB = l;
        mensagem = m;
    }
    
    @Override  
    public String toString() {
        return mensagem;
    }

}
