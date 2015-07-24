package br.ufba.lasid.jds.prototyping.hddss.instances;


import br.ufba.lasid.jds.prototyping.hddss.Message;
import br.ufba.lasid.jds.prototyping.hddss.Message;
import java.io.Serializable;

public class Content_AmoebaSequencer implements Serializable {
    int[] missing;
    int LastDLV;
    int LastACK;
    IntegerSet ACKS;
    IntegerSet DLVS;
    String content;
    Message contentMsg;
    Content_Acknowledge [] vack;

    Content_AmoebaSequencer(int lastACK, String content)
    {
        this.LastACK = lastACK;
        this.content = content; 
        ACKS = new IntegerSet();
        DLVS = new IntegerSet();
    }    
    
    public int[] getMissing()
    {
        return missing;
    }
    
    public String getContent() {
        return content;
    }

    public int getLastACK()
    {
        return LastACK;
    }

    public Message getContentMsg() {
        
        return contentMsg;
    }    
    
    public int getLastDLV()
    {
        return LastDLV;
    }    
    
    public String toString() {
        return " (lastACK = " + Integer.toString(LastACK) + ")";
     }

}
