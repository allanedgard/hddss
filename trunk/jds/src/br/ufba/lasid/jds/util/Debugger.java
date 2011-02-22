/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

/**
 *
 * @author aliriosa
 */
public class Debugger {

    public static boolean debug = true;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Debugger.debug = debug;
    }

    public static void debug(String txt){
        //if(txt.matches("[s4]") || txt.matches("[p4]") || txt.matches("[ID=4]"))
        if(isDebug())  System.out.println(txt);
    }



}
