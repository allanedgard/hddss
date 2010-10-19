/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public abstract class Clock {

    public static final String TAG = "clock";
    
    public abstract long value();
    public abstract long tickValue();

    public abstract void setMode(String v);
    public abstract char getMode();

    public abstract void adjustValue(long v);
    public abstract void adjustTickValue(long v);
    public abstract void adjustCorrection(long c);


}
