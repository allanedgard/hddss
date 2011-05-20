/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.util.TimeVarying1stOrderEMA;
import br.ufba.lasid.jds.util.XObject;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class AutonomicPBFTServer extends PBFTServer{
   
   protected TimeVarying1stOrderEMA EMAInFlowEstimator = new TimeVarying1stOrderEMA(1000, true);
   protected TimeVarying1stOrderEMA EMAInBitsEstimator = new TimeVarying1stOrderEMA(1000, true);
   protected TimeVarying1stOrderEMA EMAInMinPPEstimator = new TimeVarying1stOrderEMA(120000, true);
   protected double accInBits  = 0.0;
   protected double meanInBits = -1.0;
   protected double meanInFlow = -1.0;
   protected double meanIATime = 0.0;
   protected double oldTime = -1;
   protected double minPPDelay = -1;

   @Override
   public synchronized void handle(PBFTRequest r) {
      doRequestSensing(r);
      super.handle(r);
   }


   public void doRequestSensing(PBFTRequest r){
      try {
         double curTime = getClockValue();
         byte[] inBytes = XObject.objectToByteArray(r);
         double inBits = inBytes.length * 8;
         
         if(oldTime < 0){
            oldTime = curTime;
         }

         if(oldTime == curTime){
            accInBits += inBits;
            return;
         }
         
         double currInBits = inBits + accInBits;
         double inFlowBits = currInBits/(curTime - oldTime);
         
         meanInBits = EMAInBitsEstimator.estimate(currInBits, curTime);
         meanInFlow = EMAInFlowEstimator.estimate(inFlowBits, curTime);
         meanIATime = meanInBits / meanInFlow;

         accInBits = 0.0;
         oldTime = curTime;


         if(minPPDelay >= 0.0  && isPrimary()){
            long stBatchTimeout = (long)Math.ceil(minPPDelay * meanInBits);
            setBatchTimeout(stBatchTimeout);
         }
         

      } catch (IOException ex) {
         Logger.getLogger(AutonomicPBFTServer.class.getName()).log(Level.SEVERE, null, ex);
      }
   }


   @Override
   public void emitPrePrepare(PBFTPrePrepare pp) {

      if(pp != null && pp.getSequenceNumber() != null && !pplog.containsKey(pp.getSequenceNumber())){
         PPEventInfo info = new PPEventInfo(pp.getSequenceNumber(), getClockValue());
         pplog.put(pp.getSequenceNumber(), info);
      }
      
      super.emitPrePrepare(pp);
   }


   @Override
   public void executeBatchCommand(PBFTPrePrepare preprepare) {
      if(isPrimary() && preprepare != null){

         long currSEQ = preprepare.getSequenceNumber();
         
         if(pplog.containsKey(currSEQ)){

            long send = pplog.get(currSEQ).sendTime;
            long recv = pplog.get(currSEQ).recvTime = getClockValue();
            long delay = (recv - send);
            
            try{
               
               byte[] b = XObject.objectToByteArray(preprepare);

               double bits = b.length * 8;

               double d = delay/bits;

               if(minPPDelay < 0 || minPPDelay > d){
                  minPPDelay = d;
                  EMAInMinPPEstimator.setMean(d);
               }

               minPPDelay = EMAInMinPPEstimator.estimate(d, oldTime);

               //System.out.println(currSEQ + ";" + send + ";" + recv + ";" + delay + ";" + d + ";" + minPPDelay);
               
            }catch(Exception e){

            }
         }
      }

      super.executeBatchCommand(preprepare);
   }
   

   LogPPEventInfo pplog = new LogPPEventInfo();
   
   class PPEventInfo{
      Long sequence;
      Long sendTime;
      Long recvTime;

      PPEventInfo(Long sequence, Long sendTime){
         this.sequence = sequence;
         this.sendTime = sendTime;
      }
   }

   class LogPPEventInfo extends Hashtable<Long, PPEventInfo>{
      
   }
}
