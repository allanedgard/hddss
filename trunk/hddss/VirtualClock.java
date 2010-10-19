/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class VirtualClock extends Clock{
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
        return clock;
    }

    public void synchTick(){
        tick++;
        if (tick > nticks) {
            clock++;
            tick=0;
        }
    }

    public void asynchTick(){
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
    }

    public void tick(){
        if(mode == SYNCHMODE){

            synchTick();

        }else{

            asynchTick();
            
        }
    }

    public long tickValue(){
        return tick;
    }

    @Override
    public void setMode(String v) {
        mode = v.charAt(0);
    }

    public void adjustCorrection(long c){
        CORR = c;
    }
    public void adjustValue(long v){
        clock = v;
    }

    public void adjustTickValue(long v){
        tick = v;
    }

    @Override
    public char getMode() {
        return mode;
    }
}
