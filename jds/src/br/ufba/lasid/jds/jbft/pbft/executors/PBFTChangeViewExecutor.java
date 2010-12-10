/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTTuple;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestRetransmistionScheduler;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeViewExecutor extends PBFTServerExecutor{

    public PBFTChangeViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    /**
     * [TODO]
     * @param act
     */
    @Override
    public synchronized void execute(Action act) {
        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID() + "] "
          + "calls change view procedure at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );

       makeChangeViewRequest((PBFTMessage) act.getWrapper());

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID() + "] "
          + "multicasts change view message at time "
          + ((PBFT)getProtocol()).getTimestamp()          
        );

       //scheduleRetransmission(m);

    }

   public PBFTMessage makeChangeViewRequest(PBFTMessage m){

       PBFTMessage cv = new PBFTChangeViewMessage();

       Integer newView =
               new Integer(((PBFT)getProtocol()).getCurrentView().intValue() + 1);

       ((PBFT)getProtocol()).initPrePrepareStateInformation();
       ((PBFT)getProtocol()).initPrepareStateInformation();

       long lowCheckpointWaterMark = ((PBFT)getProtocol()).getCheckpointLowWaterMark().longValue();
       long highCheckpointWaterMark = ((PBFT)getProtocol()).getCheckpointHighWaterMark().longValue();
       
       Buffer pBuffer = getPreparedRequests(lowCheckpointWaterMark, highCheckpointWaterMark);
       Buffer ppBuffer = getPrePreparedRequests(lowCheckpointWaterMark, highCheckpointWaterMark);

        for(Object item : ppBuffer){

            PBFTMessage preprepare = (PBFTMessage) item;
            Integer view = (Integer) preprepare.get(PBFTMessage.VIEWFIELD);
            Long seqn = (Long) preprepare.get(PBFTMessage.SEQUENCENUMBERFIELD);

            ((PBFT)getProtocol()).updatePrePrepareStateInformation(preprepare, view, seqn);

       }

        for(Object item : pBuffer){

            PBFTMessage prepare = (PBFTMessage) item;
            Integer view = (Integer) prepare.get(PBFTMessage.VIEWFIELD);
            Long seqn = (Long) prepare.get(PBFTMessage.SEQUENCENUMBERFIELD);

            ((PBFT)getProtocol()).updatePrepareStateInformation(prepare, view, seqn);

       }

       PBFTTuple Q = ((PBFT)getProtocol()).getPrePrepareStateInformation();
       PBFTTuple P = ((PBFT)getProtocol()).getPrepareStateInformation();
       
       cv.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVECHANGEVIEW);
       cv.put(PBFTMessage.TIMESTAMPFIELD, ((PBFT)getProtocol()).getTimestamp());
       cv.put(PBFTMessage.VIEWFIELD, newView);
       cv.put(PBFTMessage.CHECKPOINTNUMBERFIELD, ((PBFT)getProtocol()).getLastCheckpointSequenceNumber());

       if(Q != null) cv.put(PBFTMessage.SETPREPREPAREINFORMATIONFIELD, Q);
       if(P != null) cv.put(PBFTMessage.SETPREPAREINFORMATIONFIELD, P);

       cv.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());
       
       cv = makeDisgest(cv);
       cv = encrypt(cv);

       Group g = ((PBFT)getProtocol()).getLocalGroup();

        getProtocol().getCommunicator().multicast(cv, g);

        return cv;
   }

   public Buffer getBufferedRequests(Buffer buffer, long lowCheckpointWaterMark, long highCheckpointWaterMark){

       Buffer set = new Buffer();

      for(Object item : buffer){

          PBFTMessage preprepare = (PBFTMessage) item;

          long seqn = ((Long)(preprepare.get(PBFTMessage.SEQUENCENUMBERFIELD))).longValue();

          if(seqn > lowCheckpointWaterMark && seqn <=highCheckpointWaterMark){

              set.add(preprepare);

          }
      }

      return set;
       
   }

   public Buffer getPreparedRequests(long lowCheckpointWaterMark, long highCheckpointWaterMark){

       return getBufferedRequests(
               ((PBFT)getProtocol()).getPrepareBuffer(),
               lowCheckpointWaterMark,
               highCheckpointWaterMark
       );
   }
   
   public Buffer getPrePreparedRequests(long lowCheckpointWaterMark, long highCheckpointWaterMark){

       return getBufferedRequests(
               ((PBFT)getProtocol()).getPrePrepareBuffer(),
               lowCheckpointWaterMark,
               highCheckpointWaterMark
       );
       
   }
  
    public void scheduleRetransmission(PBFTMessage m){

        Long timeout   = ((PBFT)getProtocol()).getRetransmissionTimeout();
        Long timestamp =((PBFT)getProtocol()).getTimestamp();
        long rttime = timestamp.intValue() + timeout.longValue();

        PBFTRequestRetransmistionScheduler scheduler =
                (PBFTRequestRetransmistionScheduler)(((PBFT)getProtocol()).getClientScheduler());

        m.put(scheduler.getTAG(), rttime);

        scheduler.schedule(m, rttime);

        ((PBFT)getProtocol()).getDebugger().debug(
            "["+ getClass().getSimpleName()+ ".scheduleRetransmission] "
          + "scheduling of (" + m + ") for time " + rttime
         );

    }
    
}
