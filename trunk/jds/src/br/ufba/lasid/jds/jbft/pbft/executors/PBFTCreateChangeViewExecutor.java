/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendChangeViewAction;
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
        
        PBFTMessage m = makeChangeViewRequest();

        /* after creating, it buffers and sends the view-change message */
        getProtocol().perform(new BufferChangeViewAction(m));
        getProtocol().perform(new SendChangeViewAction(m));
        
    }

    public Buffer collectIDSet(Buffer set, Buffer LOG){

       for(Object item : LOG){

           PBFTMessage m = (PBFTMessage) item;

           String srvID = getProtocol().getLocalProcess().getID().toString();
           String sqnum = m.get(PBFTMessage.SEQUENCENUMBERFIELD).toString();
           String viewn = m.get(PBFTMessage.VIEWFIELD).toString();
           String digst = m.get(PBFTMessage.DIGESTFIELD).toString();

           String ID = srvID + ":" + sqnum + ":" + viewn + ":" + digst;

           if(set.contains(ID)){

               set.add(ID);

           }

       }

       return set;
    }
   public PBFTMessage makeChangeViewRequest(){

       PBFTMessage m = new PBFTChangeViewMessage();

       Buffer prepreparedSet    = ((PBFT)getProtocol()).getPrePrepareBuffer();
       Buffer preparedSet       = ((PBFT)getProtocol()).getPrepareBuffer();
       Buffer checkpointSet     = ((PBFT)getProtocol()).getCommittedBuffer();

       Buffer Q = collectIDSet(new Buffer(), prepreparedSet);
       Buffer P = collectIDSet(new Buffer(), preparedSet   );
       Buffer C = collectIDSet(new Buffer(), checkpointSet );

       int step    = ((PBFT)getProtocol()).getViewChangeAttemps() + 1;
       int newView = ((PBFT)getProtocol()).getCurrentView().intValue() + step;
       long lwm    = ((PBFT)getProtocol()).getCheckpointLowWaterMark();

       m.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVECHANGEVIEW);
       m.put(PBFTMessage.VIEWFIELD, newView);
       m.put(PBFTMessage.CHECKPOINTLOWWATERMARK, lwm);
       m.put(PBFTMessage.SETPREPREPAREINFORMATIONFIELD, Q);
       m.put(PBFTMessage.SETPREPAREINFORMATIONFIELD, P);
       m.put(PBFTMessage.SETCHECKPOINTEDINFORMATIONFIELD, C);
       m.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());
       
       m = makeDisgest(m);
       m = encrypt(m);

       return m;
       
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
/*

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

    private Buffer getCheckpointedRequests(long lowCheckpointWaterMark, long highCheckpointWaterMark) {
       return getBufferedRequests(
               ((PBFT)getProtocol()).getCommittedBuffer(),
               lowCheckpointWaterMark,
               highCheckpointWaterMark
       );
    }

 * 
 */

}
