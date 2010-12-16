/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

/**
 *
 * @author allan
 */

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewViewMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTNewViewExecutor extends PBFTServerExecutor{
    
    public PBFTNewViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

   @Override
    public synchronized void execute(Action act) {
       
        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "is going to execute new view procedure at time "
          + ((PBFT)getProtocol()).getTimestamp()
        );

        PBFTMessage m = makeNeWView();

        Group g = ((PBFT)getProtocol()).getLocalGroup();
        
        getProtocol().getCommunicator().multicast(m, g);


    }

   public PBFTMessage makeNeWView(){

       Buffer viewchanges = ((PBFT)getProtocol()).getChangeViewBuffer();

       Buffer C = new Buffer();
       Buffer Q = new Buffer();
       Buffer P = new Buffer();

       long checkpointWaterMark = -1;
       
       int newView = Integer.MAX_VALUE;
       
       for(Object item : viewchanges){

           PBFTMessage m = (PBFTMessage) item;

           Buffer C1  = (Buffer) m.get(PBFTMessage.SETCHECKPOINTEDINFORMATIONFIELD);
           Buffer Q1  = (Buffer) m.get(PBFTMessage.SETPREPREPAREINFORMATIONFIELD);
           Buffer P1  = (Buffer) m.get(PBFTMessage.SETPREPAREINFORMATIONFIELD);

           long checkpointWaterMark1 = (Long) m.get(PBFTMessage.CHECKPOINTLOWWATERMARK);

           int view = (Integer) m.get(PBFTMessage.VIEWFIELD);

           if(view < newView){

               newView = view;
               
           }

           if(checkpointWaterMark < checkpointWaterMark1){

               checkpointWaterMark = checkpointWaterMark1;

           }

           C.addAll(C1);
           Q.addAll(Q1);
           P.addAll(P1);

       }

       if(newView == Integer.MAX_VALUE){
           newView = ((PBFT)getProtocol()).getCurrentView() + 1;
       }

       PBFTMessage nv = new PBFTNewViewMessage();

       nv.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVENEWVIEW);
       nv.put(PBFTMessage.SETPREPREPAREINFORMATIONFIELD, Q);
       nv.put(PBFTMessage.SETPREPAREINFORMATIONFIELD, P);
       nv.put(PBFTMessage.SETCHECKPOINTEDINFORMATIONFIELD, C);
       nv.put(PBFTMessage.VIEWFIELD, newView);
       nv.put(PBFTMessage.CHECKPOINTLOWWATERMARK, checkpointWaterMark);
       nv.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());

       nv = encrypt(nv);
       nv = makeDisgest(nv);       

       return nv;
       
   }

}
