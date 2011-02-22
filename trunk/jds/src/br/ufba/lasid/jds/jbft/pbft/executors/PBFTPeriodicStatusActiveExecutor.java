/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTTimeoutDetector;
import br.ufba.lasid.jds.util.ITask;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.util.Debugger;

/**
 *
 * @author aliriosa
 */
public class PBFTPeriodicStatusActiveExecutor extends PBFTExecutor<PBFTStatusActive>{

    public PBFTPeriodicStatusActiveExecutor(){
        
    }
    public PBFTPeriodicStatusActiveExecutor(PBFTServer pbftServer) {
        setProtocol(pbftServer);
    }

    public void execute(PBFTStatusActive sa) {
        PBFTServer pbft = (PBFTServer)getProtocol();
        
        long nextPP = pbft.getNextPrePrepareSEQ();
        long nextP  = pbft.getNextPrepareSEQ();
        long nextC  = pbft.getNextCommitSEQ();

        if(nextP < nextPP || nextC < nextP)
            emit(sa);
    }

    public void execute() {
        PBFTTimeoutDetector task = new PBFTTimeoutDetector() {

            @Override
            public void onTimeout() {
                
                //revokeSchedule(this);

                execute(createStatusActiveMessage());

                execute();

            }
        };

        schedule(task);
              
    }

    protected void revokeSchedule(ITask task){
//        PBFTServer pbft = (PBFTServer)getProtocol();
//        pbft.getScheduler().cancel(task);
    }

    protected void schedule(ITask task){

        PBFTServer pbft = (PBFTServer)getProtocol();
        long timestamp = pbft.getClock().value();
        long period = pbft.getSendStatusPeriod();
        long timeout = timestamp + period;
        
        pbft.getScheduler().schedule(task, timeout);
    }

    public PBFTStatusActive createStatusActiveMessage(){

        PBFTServer pbft = (PBFTServer)getProtocol();
        long ppSEQ = pbft.getNextPrePrepareSEQ() - 1L;
        long pSEQ = pbft.getNextPrepareSEQ() - 1L;
        long cSEQ = pbft.getNextCommitSEQ()  - 1L;
        long eSEQ = pbft.getNextExecuteSEQ() - 1L;
        long ckSEQ = pbft.getCheckpointLowWaterMark();
        
        return new PBFTStatusActive(
                        pbft.getLocalServerID(),
                        pbft.getCurrentViewNumber(),
                        ppSEQ, pSEQ, cSEQ, eSEQ, ckSEQ
                   );
        
    }
    public void emit(PBFTStatusActive sa){
        PBFTServer pbft = (PBFTServer)getProtocol();

        SignedMessage m;

        try {

            m = pbft.getAuthenticator().encrypt(sa);

            IGroup  g  = pbft.getLocalGroup();
            IProcess s = pbft.getLocalProcess();

            PDU pdu = new PDU();
            pdu.setSource(s);
            pdu.setDestination(g);
            pdu.setPayload(m);

            pbft.getCommunicator().multicast(pdu, g);

            Debugger.debug(
              "[PBFTPeriodicStatusActiveExecutor]s" +  pbft.getLocalServerID() +
              " sent " + sa + " at timestamp " + pbft.getClock().value() +
              " to group " + pbft.getLocalGroup() + "."
            );


        } catch (Exception ex) {
            Logger.getLogger(PBFTPeriodicStatusActiveExecutor.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        
    }

}
