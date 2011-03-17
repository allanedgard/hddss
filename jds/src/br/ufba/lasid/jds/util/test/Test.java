/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util.test;

import br.ufba.lasid.jds.util.XObject;
import java.io.Serializable;

/**
 *
 * @author aliriosa
 */
public class Test {

    public static void main(String[] args) throws Exception{
            Integer X = new Integer(45);

            A a = new A();
            a.X = X;

            byte[] bytes = XObject.objectToByteArray(a);

    }   

}

    class A implements Serializable{

        Object X;

        public void print(){
            System.out.println("Alo Mundo");
        }
    }


  


