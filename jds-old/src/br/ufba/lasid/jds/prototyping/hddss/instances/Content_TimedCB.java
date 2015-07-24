package br.ufba.lasid.jds.prototyping.hddss.instances;

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
    String message;
    int [] lastMsgTimeStamp;
    Content_Acknowledge [] vack;
    String cont;

    Content_TimedCB(String m, int l, Content_Acknowledge [] acks) {
        LCB = l;
        message = m;
        vack = acks;
    }

    Content_TimedCB(String m, int l, Content_Acknowledge [] acks, int size) {
        LCB = l;
        message = m;
        vack = acks;
        cont = "";
        for (int i=0;i<size; i++)
            cont = cont + "*";
    }

    Content_TimedCB(String m, int l, int [] u) {
        LCB = l;
        message = m;
        lastMsgTimeStamp = u;
    }

    Content_TimedCB(String m, int l, int [] u, int size) {
        LCB = l;
        message = m;
        lastMsgTimeStamp = u;
        cont = "";
        for (int i=0;i<size; i++)
            cont = cont + "*";
    }

    Content_TimedCB(String m, int l) {
        LCB = l;
        message = m;
    }
    
    @Override  
    public String toString() {
        return message;
    }

}
