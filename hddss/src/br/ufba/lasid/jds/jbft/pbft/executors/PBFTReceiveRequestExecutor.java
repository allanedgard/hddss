/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerReceiveRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BatchTimeoutAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTBatchingTimeoutScheduler;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTPrimaryFDScheduler;
import br.ufba.lasid.jds.security.Authenticator;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveRequestExecutor extends ClientServerReceiveRequestExecutor{

    PBFTMessage batch = null;
    
    public PBFTReceiveRequestExecutor(Protocol protocol) {
        super(protocol);
    }

    public boolean isABatchTimeoutAction(Action act){
        return BatchTimeoutAction.class.equals(act.getClass());
    }
    @Override
    public synchronized void execute(Action act) {

        if(isABatchTimeoutAction(act)){
            makePrePrepare(batch);
            return;
        }
        
        PBFTMessage m = (PBFTMessage)act.getMessage();
        
        Authenticator authenticator =
                ((PBFT)getProtocol()).getClientMessageAuthenticator();

        if(authenticator.check(m)){

            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTReceiveRequestExecutor.execute] client request " + m
              + " was authenticated at time " + ((PBFT)getProtocol()).getTimestamp()
             );
            
            if(addRequestToBuffer(m)){

                ((PBFT)getProtocol()).getDebugger().debug(
                    "[PBFTReceiveRequestExecutor.execute] client request " + m
                  + " was buffered in server(p" + getProtocol().getLocalProcess().getID() + ") "
                  + " at time " + ((PBFT)getProtocol()).getTimestamp()
                 );

                if(((PBFT)getProtocol()).isPrimary()){

                    if(((PBFT)getProtocol()).batchIsNotComplete()){

                        makeBatching(m);
                        
                    }else{

                        makePrePrepare(batch);

                    }

                    return;
                }

                scheduleChangeView(m);
            }
        }
    }

    public Buffer getRequestBuffer(){
        return ((PBFT)getProtocol()).getRequestBuffer();
    }
    
    /*
     *  [REVIEW]
     */
    public PBFTMessage makePrePrepare(PBFTMessage request){
        if(request == null)
            return null;
        
        Authenticator authenticator =
                ((PBFT)getProtocol()).getServerAuthenticator();

        PBFTMessage pp = new PBFTMessage();

        pp.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.PREPREPARE);
        pp.put(PBFTMessage.REQUESTFIELD, request);
        pp.put(PBFTMessage.VIEWFIELD, ((PBFT)getProtocol()).getCurrentView());
        pp.put(PBFTMessage.SEQUENCENUMBERFIELD, PBFTMessage.newSequenceNumber());
        pp.put(PBFTMessage.SOURCEFIELD, getProtocol().getLocalProcess());

        pp = (PBFTMessage)authenticator.makeDisgest(pp);
        pp = (PBFTMessage)authenticator.encrypt(pp);

        getProtocol().getCommunicator().multicast(
            pp, ((PBFT)getProtocol()).getLocalGroup()
        );

        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTReceiveRequestExecutor.execute] preprepare " + pp
          + " was sending by server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );

        batch = null;
        ((PBFT)getProtocol()).initBatching();

        return pp;
        
    }

    private boolean addRequestToBuffer(PBFTMessage request) {
        
        if(getRequestBuffer().contains(request)){
            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTReceiveRequestExecutor.execute] client request " + request
              + " was rejected in server(p" + getProtocol().getLocalProcess().getID() + ") "
              + " at time " + ((PBFT)getProtocol()).getTimestamp() + " "
              + "because it's already in buffer."
             );

            return false;
        }
        
        getRequestBuffer().add(request);

        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTReceiveRequestExecutor.execute] client request " + request
          + " was buffered in server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );

        return true;
        
    }

    public void scheduleChangeView(PBFTMessage m){

        Long timeout   = ((PBFT)getProtocol()).getPrimaryFaultyTimeout();
        Long timestamp =((PBFT)getProtocol()).getTimestamp();
        
        Long rttime = new Long(timestamp.intValue() + timeout.longValue());

        PBFTPrimaryFDScheduler scheduler =
                (PBFTPrimaryFDScheduler)(((PBFT)getProtocol()).getPrimaryFDScheduler());

        m.put(
          scheduler.getTAG(),
          ((PBFT)getProtocol()).getPrimaryFaultyTimeout()
        );


        scheduler.schedule(m);

        ((PBFT)getProtocol()).getDebugger().debug(
            "["+ getClass().getSimpleName()+ ".scheduleChangeView] "
          + "scheduling of (" + m + ") for time " + rttime + " "
          + "at server(" + getProtocol().getLocalProcess().getID() + ")"
         );
    }

    public PBFTMessage makeBatching(PBFTMessage request){
        
        if(batch == null){

            batch = new PBFTMessage();
            batch.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVEREQUEST);
            batch.put(PBFTMessage.TIMESTAMPFIELD, ((PBFT)getProtocol()).getTimestamp());
            batch.put(PBFTMessage.CLIENTFIELD, getProtocol().getLocalProcess());
            batch.put(PBFT.BATCHINGTIMEOUT, ((PBFT)getProtocol()).getBatchingTimeout());

            PBFTBatchingTimeoutScheduler scheduler =
                (PBFTBatchingTimeoutScheduler)((PBFT)getProtocol()).getBatchingScheduler();
            
            scheduler.schedule(batch);
            
        }

        batch.put(((PBFT)getProtocol()).getRequestField(), request);

        ((PBFT)getProtocol()).increaseBatch();

        batch.put(PBFTMessage.BATCHSIZEFIELD, ((PBFT)getProtocol()).getBatchingCount());
        
        return batch;
    }



}
