/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerReceiveRequestExecutor;
import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.util.Scheduler;
import br.ufba.lasid.jds.util.Task;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.ChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTPrimaryFDScheduler;
import br.ufba.lasid.jds.security.Authenticator;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveRequestExecutor extends ClientServerReceiveRequestExecutor{

    public PBFTReceiveRequestExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage)act.getMessage();
        
        Authenticator authenticator =
                ((PBFT)getProtocol()).getClientMessageAuthenticator();

        if(authenticator.check(m)){

            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTReceiveRequestExecutor.execute] client request " + m
              + " was authenticated at time " + ((PBFT)getProtocol()).getTimestamp()
             );
            
            addRequestToBuffer(m);

            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTReceiveRequestExecutor.execute] client request " + m
              + " was buffered in server(p" + getProtocol().getLocalProcess().getID() + ") "
              + " at time " + ((PBFT)getProtocol()).getTimestamp()
             );

            if(((PBFT)getProtocol()).isPrimary()){
                makePrePrepare(m);
                return;
            }
            
            scheduleChangeView(m);
        }
    }

    public Buffer getRequestBuffer(){
        return ((PBFT)getProtocol()).getRequestBuffer();
    }
    
    /*
     *  [REVIEW]
     */
    public PBFTMessage makePrePrepare(PBFTMessage request){
                
        Authenticator authenticator =
                ((PBFT)getProtocol()).getServerAuthenticator();

        PBFTMessage pp = new PBFTMessage();

        pp.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.PREPREPARE);
        pp.put(PBFTMessage.REQUESTFIELD, request);
        pp.put(PBFTMessage.VIEWFIELD, ((PBFT)getProtocol()).getCurrentView());
        pp.put(PBFTMessage.SEQUENCENUMBERFIELD, PBFTMessage.newSequenceNumber());

        pp = (PBFTMessage)authenticator.digest(pp);
        
        getProtocol().getCommunicator().multicast(
            pp, ((PBFT)getProtocol()).getLocalGroup()
        );
        
        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTReceiveRequestExecutor.execute] preprepare " + pp
          + " was sending by server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp() 
         );
        
        return pp;
        
    }

    private void addRequestToBuffer(PBFTMessage request) {
        
        if(getRequestBuffer().contains(request)){
            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTReceiveRequestExecutor.execute] client request " + request
              + " was rejected in server(p" + getProtocol().getLocalProcess().getID() + ") "
              + " at time " + ((PBFT)getProtocol()).getTimestamp() + " "
              + "because it's already in buffer."
             );

            return;
        }
        
        getRequestBuffer().add(request);

        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTReceiveRequestExecutor.execute] client request " + request
          + " was buffered in server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );

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



}
