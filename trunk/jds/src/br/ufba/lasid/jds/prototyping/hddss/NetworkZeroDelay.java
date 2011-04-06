package br.ufba.lasid.jds.prototyping.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class NetworkZeroDelay extends Network{

    @Override
    double delay(Message m) {
        return 0.0;
    }

}
