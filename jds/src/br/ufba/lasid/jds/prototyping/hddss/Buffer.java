package br.ufba.lasid.jds.prototyping.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
import java.util.*;
import java.util.ArrayList;

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
     
     public boolean checkTime(int time) {
         if ( inside.containsKey(time) ) {
             return true;
         }
         else return false;
         
     }

     synchronized int totalMsgs(int tempo) {
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

     synchronized void forward(int dtime){

        if(!inside.isEmpty()){
           ArrayList<Integer> times = new ArrayList<Integer>(inside.keySet());
           TreeMap temp = new TreeMap(inside);

           inside.clear();

           for(int time : times){
              Object obj = temp.get(time);
              inside.put(time + dtime, obj);
           }

           times.clear();
           temp.clear();

           times = null;
           temp = null;
        }
     }
     
     synchronized int getLast() {
        try{
         return ((Integer) inside.lastKey()).intValue();
        }catch(Exception e){
           return -1;
        }
     }

     synchronized int getFist() {
        try{
         return ((Integer) inside.firstKey()).intValue();
        }catch(Exception e){
           return -1;
        }
     }

    @Override
    public String toString() {
        return "Buffer{" + "inside=" + inside + '}';
    }
    
}
