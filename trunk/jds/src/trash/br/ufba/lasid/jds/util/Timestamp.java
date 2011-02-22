/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.util;

import br.ufba.lasid.jds.util.IClock;
import trash.br.ufba.lasid.jds.IData;

/**
 *
 * @author aliriosa
 */
public final class Timestamp implements IData, Comparable<Timestamp>{

    protected long time;
    protected long seq;

    public Timestamp (IClock clock){
        this(clock, Long.MIN_VALUE);
    }

    public Timestamp (IClock clock, long seq){
        this(clock.value(), seq);
    }

    public Timestamp(long time, long seq){
        this.time = time;
        this.seq  = seq;
    }
    
    public int compareTo(Timestamp o) {
        
        if(o != null){
            
            if(time == o.time && seq == o.seq){
                return 0;
            }

            if((time > o.time && seq > o.seq) || (time < o.time && seq > o.seq)){
                return 1;
            }

            if((time < o.time && seq < o.seq) || (time > o.time && seq < o.seq)){
                return -1;
            }

        }

        return -1;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj != null && obj instanceof Timestamp){

            Timestamp t = (Timestamp) obj;

            return (compareTo(t) == 0);
            
        }

        return false;
        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (int) (this.time ^ (this.time >>> 32));
        hash = 61 * hash + (int) (this.seq ^ (this.seq >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "Timestamp{" + "time=" + time + ", seq=" + seq + "}";
    }

}
