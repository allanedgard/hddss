package br.ufba.lasid.jds.prototyping.hddss;

public class Clock_Virtual extends AbstractClock {
    private long clock = 0;
    private long clockAnterior = -1;
    private long tick = 0;
    private static long nticks = 10000;
    private long CORR = 0;
    double rho = 0;
    private char mode = 's';
    private static int count = 0;
    private int SERIAL = 0;

    public final char SYNCHMODE = 's';
    public final char ASYNCHMODE = 'a';
    
    Clock_Virtual () {
        SERIAL = count++;
    }
    
    @Override
    public long value() {
//        synchronized(this){
            return clock;
  //      }
    }
    
    public static void setNTicks(int nt) {
        nticks = nt;
    }
    
    public static long getNTicks() {
        return nticks;
    }

    public boolean synchTick(){
        synchronized(this){
            boolean result=false;
            tick++;
            if (tick == nticks) {
                clock++;
                tick=0;
                result= true;
            }
                /* System.out.println("relogio R"+SERIAL+" rho = "+rho+ 
                        " clock="+clock+
                        " tick="+tick); */
            return result;
        }
    }

    public boolean asynchTick(){
        synchronized(this){
            if (CORR==0) {
                boolean result=false;
                tick++;
                if (tick == nticks+rho) {
                    clock++;
                    tick=0;
                    result=true;
                }
                /* System.out.println("relogio R"+SERIAL+" rho = "+rho+ 
                        " clock="+clock+
                        " tick="+tick); */
                return result;
            } else {
                CORR--;
            }
            return false;
        }
    }

    public boolean tick(){
        synchronized(this){
            if(mode == SYNCHMODE){

                return synchTick();

            }else{

                return asynchTick();

            }
        }
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
