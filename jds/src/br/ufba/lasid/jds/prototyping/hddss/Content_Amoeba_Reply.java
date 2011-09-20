package br.ufba.lasid.jds.prototyping.hddss;


/**
 *
 * @author Anne
 */
public class Content_Amoeba_Reply {
    private int accept;
    private Message content;
    private String value;
    Content_Acknowledge [] vack;

    Content_Amoeba_Reply(int accept, Message content)
    {
        this.accept = accept;
        this.content = content;
    }

    Content_Amoeba_Reply(int accept, Message content, Content_Acknowledge [] ack)
    {
        this.accept = accept;
        this.content = content;
        vack = ack;
    }
    
    Content_Amoeba_Reply(int accept, String value)
    {
        this.accept = accept;
        this.value = value;
    }    
    
    public String getValue() {
        return value;
    }

    public Message getContent() {
        return content;
    }    
    
    public int getAccept()
    {
        return accept;
    }

    public String toString() {
        return " (msg = " + content.toString() + ")";
     }

}
