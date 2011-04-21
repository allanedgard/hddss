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
    double processingTime;
    public void setProcessingTime(String delay){
       processingTime = Double.parseDouble(delay);
    }
    @Override
    double delay(Message m) {
        return processingTime;
    }

}
