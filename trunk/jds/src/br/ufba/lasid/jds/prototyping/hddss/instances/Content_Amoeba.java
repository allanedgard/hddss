/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.instances;

import br.ufba.lasid.jds.prototyping.hddss.Message;


/**
 *
 * @author Anne
 */
public class Content_Amoeba {
    int[] missing;
    int accept;
    int last;
    String content;
    Message contentMsg;
    Content_Acknowledge [] vack;

    Content_Amoeba(int last, int atual)
    {
        this.last = last - 1;
        int total = atual - last;
        missing = new int[total];
        missing[0] = last;
        for(int i=1; i<total; i++)
            missing[i] = missing[i-1]++;
    }
    
    Content_Amoeba(int last, int atual,  Content_Acknowledge [] acks)
    {
        this.last = last - 1;
        int total = atual - last;
        missing = new int[total];
        missing[0] = last;
        for(int i=1; i<total; i++)
            missing[i] = missing[i-1]++;
        vack = acks;
    }

    Content_Amoeba(int accept, Message content, Content_Acknowledge [] ack)
    {
        this.accept = accept;
        this.contentMsg = content;
        vack = ack;
    }

    Content_Amoeba(int last, int atual, String content)
    {
        this.last = last - 1;
        int total = atual - last;
        missing = new int[total];
        missing[0] = last;
        for(int i=1; i<total; i++)
            missing[i] = missing[i-1]++;
        this.content = content; 
    }    

    Content_Amoeba(int last, String content)
    {
        this.last = last;
        this.content = content; 
    }    
    
    Content_Amoeba(int last, String content,  Content_Acknowledge [] acks)
    {
        this.last = last;
        this.content = content; 
        vack = acks;
    }  
    
    Content_Amoeba(int last){
        this.last = last - 1;
    }

    public int[] getMissing()
    {
        return missing;
    }
    
    public String getContent() {
        return content;
    }

    public int getLast()
    {
        return last;
    }

    public Message getContentMsg() {
        return contentMsg;
    }    
    
    public int getAccept()
    {
        return accept;
    }    
    
    public String toString() {
        return " (last = " + Integer.toString(last) + ")";
     }

}
