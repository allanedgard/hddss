/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.adapters;

/**
 *
 * @author aliriosa
 */
public class ClassT implements Test{

    public void print() {
        System.out.println("Alo Mundo");
    }

    public static void main(String[] args){

        Long x = Adapter.newInstance(new Long(0));
        System.out.println(x + 1);
    }

}
