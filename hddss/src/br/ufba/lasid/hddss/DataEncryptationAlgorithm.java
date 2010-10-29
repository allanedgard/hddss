/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss;

/**
 *
 * @author aliriosa
 */
public class DataEncryptationAlgorithm implements Algorithm<Object> {
    Object data;
    long latency = 0;
    public Object getOutput() {
        return data;
    }

    public void setInput(Object input) {
        this.data = input;
    }

    public void setLatency(String v){
        latency = Long.parseLong(v);
    }
    public long getLatency() {
        return latency;
    }

    public void execute(Object...args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isValid(Object data){
        //reimplement later.
        return true;
    }

}
