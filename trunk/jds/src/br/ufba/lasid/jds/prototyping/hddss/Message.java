package br.ufba.lasid.jds.prototyping.hddss;

import java.io.Serializable;

public class Message  implements Comparable<Message>, Serializable {

    public int sender;
    public int relayFrom;
    public int relayTo;
    public int destination;
    public int type;
    public int logicalClock;
    public int physicalClock;
    public int receptionTime;
    public int hops;
    public Object content;
    public boolean payload;
    
    /*FIELDS ADDED BY ALIRIO SÁ*/
    boolean multicast = false;
    transient static long SERIALNUMBER = -1;
    long serialnumber = -1;



    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }


    public Message(int r, int d, Object c){
        sender = r;
        destination = d;
        content =c ;
        serialnumber = ++SERIALNUMBER;
    }
    public Message(int r, int d, Object c, boolean m){
        sender = r;
        destination = d;
        content =c ;
        multicast = m;
        serialnumber = ++SERIALNUMBER;
    }

    /** Creates a new instance of Mensagem */
    public Message(int r, int d, int t, int rL, int rF, Object c) {
        sender = r;
        destination = d;
        type = t;
        hops =0;
        logicalClock = rL;
        physicalClock = rF;
        content = c;
        relayFrom = -1;
        relayTo = -1;
        payload = false;
        serialnumber = ++SERIALNUMBER;
    }

    public Message(int r, int d, int t, int rL, int rF, Object c, boolean m) {
        sender = r;
        destination = d;
        type = t;
        hops =0;
        logicalClock = rL;
        physicalClock = rF;
        content = c;
        relayFrom = -1;
        relayTo = -1;
        payload = false;
        multicast = m;
        serialnumber = ++SERIALNUMBER;
    }
    
    public String getId() {
        return "p"+sender+" para p"+destination+" em "+physicalClock;
    }

    @Override public boolean equals( Object aThat ) {
     if ( this == aThat ) return true;
     if ( !(aThat instanceof Message) ) return false;

     return (
                (this.destination== ((Message)aThat).destination) &&
                (this.sender==((Message)aThat).sender) &&
                (this.logicalClock==((Message)aThat).logicalClock) &&
                (this.physicalClock==((Message)aThat).physicalClock) &&
                (this.type==((Message)aThat).type) &&
                (this.content==((Message)aThat).content) );
   }

   @Override public int hashCode() {
     int result = HashCodeUtil.SEED;
     result = HashCodeUtil.hash( result,  this.logicalClock);
     result = HashCodeUtil.hash( result,  this.sender);
     return result;
   }



    public int compareTo( Message aThat ) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

    if ( this.logicalClock == this.logicalClock ) return EQUAL;

    if (this.logicalClock < aThat.logicalClock)
        return BEFORE;
    else
    if (this.logicalClock > aThat.logicalClock)
        return AFTER;
    else
    if (this.sender < aThat.sender)
        return BEFORE;
    else
    if (this.sender > aThat.sender)
        return AFTER;
    else
        return EQUAL;
  }

  @Override
    public String toString() {
                return ""+
                this.sender+"; "+
                this.destination+"; "+
                this.physicalClock+"; "+
                this.logicalClock+"; "+
                this.type+"; "+
                this.content;
  }

}
