/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

/**
 *
 * @author aliriosa
 */
public class TimeVarying1stOrderEMA {

   protected double period = 1.0;

   protected Double mean = null;

   protected boolean initWithFirstSample = false;

   protected double oldTime = 0.0;

   protected double acc = 0.0;

   public TimeVarying1stOrderEMA(double period, boolean initWithFirstSample){
      this.period = period;
      this.initWithFirstSample = initWithFirstSample;
   }

   public TimeVarying1stOrderEMA(double period){
      this(period, false);
   }

   public double estimate(double input, double curTime){

      if(mean == null){
         mean = 0.0;
         if(initWithFirstSample){
            mean = input;
         }         
         oldTime = curTime;
      }

      if(oldTime == curTime){
         acc += input;
         return mean;
      }

      double sample = input + acc;
      
      double alpha = alpha(oldTime, curTime);      
   
      mean = mean * alpha + sample * (1-alpha);

      oldTime = curTime;
      acc = 0.0;

      return mean;

   }
   
   public double alpha(double t0, double t1){
      double dt = t1 - t0;
      return Math.exp(-dt/period);
   }

   public void setMean(double mean){
      this.mean = mean;
   }

   public void setMean(double mean, double curTime){
      this.mean = mean;
      this.oldTime = curTime;
   }

}
