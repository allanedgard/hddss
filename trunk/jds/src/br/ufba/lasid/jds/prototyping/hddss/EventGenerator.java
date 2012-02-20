/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.lasid.jds.prototyping.hddss;
import java.lang.reflect.*;

public class EventGenerator {
        
        Randomize r;
        double prob, minProb, maxProb;
        Method m;
        Object o;
        int activationTime=0;
    
        void setActivationTime(int t) {
            activationTime=t;
        }    
        
        EventGenerator() {
            r = new Randomize();
        }
        
        public void setProb(String dt) {
            prob = Double.parseDouble(dt);
        }
        
        public void setMaxProb(String dt) {
            maxProb = Double.parseDouble(dt);
        }

        public void setMinProb(String dt) {
            minProb = Double.parseDouble(dt);
        }
        
        public void setAction(Object o1, String m1) {
            o = o1;
            try {
                m = o.getClass().getMethod(m1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        boolean hasEvent(int t) {
            if (t<activationTime) 
                return false;
            return hasEvent();
        }
        
        boolean hasEvent() {
            double x1;
            if (prob==0.0) {
                x1 = r.uniform(minProb, maxProb);
            } else x1 = prob;
            return (r.uniform() <= x1);
        }
        
        public void trigger() {
            if (this.hasEvent())
                try { m.invoke(o); }
                catch (Exception e) {
                    e.printStackTrace();
                }
            else {
            }
                
        }
        
        public void trigger(int t) {
            if (this.hasEvent(t))
                try { m.invoke(o); }
                catch (Exception e) {
                    e.printStackTrace();
                }
            else {
            }
                
        }
 
    
    
}
