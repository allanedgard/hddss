package br.ufba.lasid.jds.prototyping.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class NetworkDeterministic extends Network{

    @Override
    double delay() {
        return processingTime;
    }

}