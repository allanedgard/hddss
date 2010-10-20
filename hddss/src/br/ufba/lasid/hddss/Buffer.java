package br.ufba.lasid.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
import java.util.*;

public  class Buffer {
    
    int min;
    int max;

    TreeMap inside;
   
     Buffer() {
         inside = new TreeMap();
         min = -1;
         max = -1;
     };
     
     
     public synchronized void add(int time, Message msg) {
         ArrayList a;
         if (checkTime(time)==false) {
             a = new ArrayList();
             a.add(msg);
             inside.put(time, a);
         }
         else  {
             a = (ArrayList) inside.get(time);
             a.add(msg);
             inside.put(time, a);
         }
     }
     
     public synchronized ArrayList getMsgs(int time) {
         if (checkTime(time)==false) {
             return new ArrayList();
         }
         else  { 
             ArrayList a, b;
             a = (ArrayList) inside.get(time);
             
             if (a.size() == 1) {
                inside.remove(time);
                return a;
             }
             else {
                 b = new ArrayList();
                 b.add(a.get(0));
                 a.remove(0);
                 inside.put(time, a);
                 return b;
             }

         }
         
     }
     
     private boolean checkTime(int time) {
         if ( inside.containsKey(time) ) {
             return true;
         }
         else return false;
         
     }

     int totalMsgs(int tempo) {
         int j = 0;
         java.util.ArrayList a;
         for(int i=tempo;i<this.getLast();i++) {
                      if (checkTime(i)!=false) {
                            a = (ArrayList) inside.get(i);
                            j += a.size();
                        }
         }
         return j;
     }
     
     int getLast() {
         return ((Integer) inside.lastKey()).intValue();
     }
    
}
