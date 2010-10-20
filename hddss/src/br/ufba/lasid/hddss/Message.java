package br.ufba.lasid.hddss;

/*
 * Mensagem.java
 *
 * Created on 22 de Julho de 2008, 05:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */

public final class Message  implements Comparable<Message> {
    
    int sender;
    int relayFrom;
    int relayTo;
    int destination;
    int type;
    int logicalClock;
    int physicalClock;
    int tempoRecepcao;
    int hops;
    Object content;
    boolean payload;
     
    
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
