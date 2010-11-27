package br.ufba.lasid.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class Network_Deterministic extends Network{

    @Override
    double delay() {
        return processingTime;
    }

}
