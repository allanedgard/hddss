/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.test;

import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class GenericTest{

    public static void main(String[] args){
        ArrayList<String> a = new ArrayList<String>();


        ArrayList<Long> b = new ArrayList<Long>();

        System.out.println(b.getClass().getTypeParameters()[0].getName());
    }

}
