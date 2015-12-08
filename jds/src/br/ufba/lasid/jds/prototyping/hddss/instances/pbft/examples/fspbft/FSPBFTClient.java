/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.instances.pbft.examples.fspbft;

import br.ufba.lasid.jds.prototyping.hddss.Randomize;
import br.ufba.lasid.jds.prototyping.hddss.instances.pbft.SimulatedPBFTClientAgent;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public class FSPBFTClient extends SimulatedPBFTClientAgent implements IFileSystem{
    private double rgp = 0.0;
    private Randomize r = new Randomize();
    private  boolean waiting = false;
    private static int nThread = 0;

    public void setRequestGenerationProbability(String prob){
        rgp = Double.parseDouble(prob);
    }

    public boolean hasRequest(){
        return (r.uniform() <= rgp);
    }

    @Override
    public void execute() {

        if(hasRequest() && !waiting){
                waiting = true;
                new ClientCaller().start();
        }

    }
    
    public void receiveResult(IPayload content) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * It adapts to the simulator.
     */
    public void doCall(){

    }

    public long fopen(String filename, Mode mode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long fclose(long fp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long fprint(String text, long fp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String fgets(long fp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long fseek(long fp, long offset, long origin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    class ClientCaller extends Thread{

        public ClientCaller() {
            super("ClientCaller" + nThread);
            nThread ++;
        }


        @Override
        public void run() {
            doCall();

            waiting = false;

        }

    }

}
