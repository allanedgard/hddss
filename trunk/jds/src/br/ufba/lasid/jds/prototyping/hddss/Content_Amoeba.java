/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;


/**
 *
 * @author Anne
 */
public class Content_Amoeba {
    private int[] missing;
    private int last;

    Content_Amoeba(int last, int atual)
    {
        this.last = last - 1;
        int total = atual - last;
        missing = new int[total];
        missing[0] = last;
        for(int i=1; i<total; i++)
            missing[i] = missing[i-1]++;
    }

    Content_Amoeba(int last){
        this.last = last - 1;
    }

    public int[] getMissing()
    {
        return missing;
    }

    public int getLast()
    {
        return last;
    }

    public String toString() {
        return " (last = " + Integer.toString(last) + ")";
     }

}
