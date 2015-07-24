/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aliriosa
 */
public class XMath {

    public static <T extends Comparable<T>> T max(List<T> list){
        return kthmax(1, list);
    }

    public static <T extends Comparable<T>> T kthmax(int k, List<T> list){
        
        T lmax = null;
        T cmax = null;

        int lcount = 0;
        int ccount = 0;
        
        if(list == null){
            return null;
        }

        if(list.size() < k){
            return null;
        }

        while(lcount < k){
            
            for(int i = 0; i < list.size(); i++){

                T curr = list.get(i);

                if(cmax != null && cmax.compareTo(curr) == 0){
                    ccount++;
                    continue;
                }
                
                if(cmax == null || (cmax != null && curr.compareTo(cmax) > 0)){
                    if(lmax == null || (lmax != null && curr.compareTo(lmax) < 0)){
                        ccount = 1;
                        cmax = curr;
                    }//end if curr < lmax
                }//end if curr > cmax
            }//end for each element
            
            lmax = cmax;
            cmax = null;
            
            lcount += ccount;
            ccount = 0;
        }
        
        return lmax;
    }

    public static void main(String[] args){
        ArrayList<Long> a = new ArrayList<Long>();
        a.add(1L); a.add(2L); a.add(3L); a.add(4L); a.add(5L);
        System.out.println(kthmax(5, a));

    }
}
