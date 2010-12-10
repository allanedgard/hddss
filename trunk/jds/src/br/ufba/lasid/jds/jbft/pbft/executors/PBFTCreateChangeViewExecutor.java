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
import br.ufba.lasid.jds.jbft.pbft.actions.BufferChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTCreateChangeViewExecutor extends PBFTServerExecutor {

    public PBFTCreateChangeViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage cv = makeChangeViewRequest();

        getProtocol().perform(new BufferChangeViewAction(cv));
    }

   public PBFTMessage makeChangeViewRequest(){

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
    

}
