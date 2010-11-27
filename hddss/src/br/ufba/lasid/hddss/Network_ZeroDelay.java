package br.ufba.lasid.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class Network_ZeroDelay extends Network{

    @Override
    double delay() {
        return 0.0;
    }

}
