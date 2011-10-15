package br.ufba.lasid.jds.prototyping.hddss;


import br.ufba.lasid.jds.prototyping.hddss.Message;

public class Content_AmoebaSequencer {
    int[] missing;
    int LastDLV;
    int LastACK;
    IntegerSet ACKS;
    IntegerSet DLVS;
    String content;
    Message contentMsg;
    //Content_Acknowledge [] vack;

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
