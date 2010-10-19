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
    
    int minimo;
    int maximo;

    TreeMap interno;
   
     Buffer() {
         interno = new TreeMap();
         minimo = -1;
         maximo = -1;
     };
     
     
     public synchronized void adiciona(int tempo, Mensagem msg) {
         ArrayList a;
         if (verificaTempo(tempo)==false) {
             a = new ArrayList();
             a.add(msg);
             interno.put(tempo, a);
         }
         else  {
             a = (ArrayList) interno.get(tempo);
             a.add(msg);
             interno.put(tempo, a);
         }
     }
     
     public synchronized ArrayList obtemMensagens(int tempo) {
         if (verificaTempo(tempo)==false) {
             return new ArrayList();
         }
         else  { 
             ArrayList a, b;
             a = (ArrayList) interno.get(tempo);
             
             if (a.size() == 1) {
                interno.remove(tempo);
                return a;
             }
             else {
                 b = new ArrayList();
                 b.add(a.get(0));
                 a.remove(0);
                 interno.put(tempo, a);
                 return b;
             }

         }
         
     }
     
     private boolean verificaTempo(int tempo) {
         if ( interno.containsKey(tempo) ) {
             return true;
         }
         else return false;
         
     }

     int totalMsgs(int tempo) {
         int j = 0;
         java.util.ArrayList a;
         for(int i=tempo;i<this.obtemUltimo();i++) {
                      if (verificaTempo(i)!=false) {
                            a = (ArrayList) interno.get(i);
                            j += a.size();
                        }
         }
         return j;
     }
     
     int obtemUltimo() {
         return ((Integer) interno.lastKey()).intValue();
     }
    
}
