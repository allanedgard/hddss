/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.helloworldcs;

import br.ufba.lasid.jds.prototyping.hddss.Simulator;
import java.io.IOException;

/**
 *
 * @author aliriosa
 */
public class Main {
    public static void main(String[] args) throws IOException{
        System.out.println(System.getProperty("user.dir"));
        String[] __args = {System.getProperty("user.dir") + "/examples/helloworldcs/config_helloworldcs.txt"};
        Simulator.main(__args);
    }
}
