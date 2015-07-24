package br.ufba.lasid.jds.prototyping.hddss;

public abstract class AbstractClock implements IClock {

    public static final String TAG = "clock";
    
    public abstract long value();
    public abstract long tickValue();

    public abstract void setMode(String v);
    public abstract char getMode();

    public abstract void adjustValue(long v);
    public abstract void adjustTickValue(long v);
    public abstract void adjustCorrection(long c);


}
