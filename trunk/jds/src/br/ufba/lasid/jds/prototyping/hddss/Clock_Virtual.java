package br.ufba.lasid.jds.prototyping.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class Clock_Virtual extends AbstractClock {
    private long clock = 0;
    private long tick = 0;
    long nticks = 0;
    private long CORR = 0;
    double rho = 0;
    private char mode = 's';

    public final char SYNCHMODE = 's';
    public final char ASYNCHMODE = 'a';
    
    @Override
    public long value() {
//        synchronized(this){
            return clock;
  //      }
    }

    public void synchTick(){
    //    synchronized(this){
            tick++;
            if (tick > nticks) {
                clock++;
                tick=0;
            }
      //  }
    }

    public void asynchTick(){
        //synchronized(this){
            if (CORR==0) {
                tick++;
                if (tick > nticks) {
                    clock++;
                    tick=0;
                    tick += rho;
                }
            } else {
                CORR--;
            }
        //}
    }

    public void tick(){
        //synchronized(this){
            if(mode == SYNCHMODE){

                synchTick();

            }else{

                asynchTick();

            }
        //}
    }

    public long tickValue(){
        //synchronized(this){
            return tick;
        //}
    }

    @Override
    public void setMode(String v) {
        mode = v.charAt(0);
    }

    public void adjustCorrection(long c){
        //synchronized(this){
            CORR = c;
        //}
    }
    public void adjustValue(long v){
        //synchronized(this){
            clock = v;
        //}
    }

    public void adjustTickValue(long v){
        //synchronized(this){
            tick = v;
        //}
    }

    @Override
    public char getMode() {
        return mode;
    }
}
