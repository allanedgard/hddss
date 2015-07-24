/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequestInfo;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
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
   double RQMTBA = 0.0;
   double RQMTOE = 0.0; /* mean time ordering and execution of client requests */
   double RQCount = 0;

   double OEMTBA = 0.0;

   RQTable rqtable = new RQTable();
   @Override
   public synchronized void handle(PBFTRequest r) {
      doRQSensing(r);
      super.handle(r);
   }

   @Override
   public void emitPrePrepare(PBFTPrePrepare pp) {
      PBFTRequestInfo rinfo = getRequestInfo();

      for(String digest : pp.getDigests()){
         PBFTRequest r = rinfo.getRequest(digest);
         RQInfo info = rqtable.get(RQInfo.getEntry(r));
         if(info != null && info.ordrTime < 0){
            info.ordrTime = getClockValue();
         }
      }
      super.emitPrePrepare(pp);
   }


   
   public void doRQSensing(PBFTRequest r){      
      
         updateRQInInformation(r);
         
         RQMTBA = computeRQMTBA();

         long bt = (long)Math.ceil(RQMTOE);
         
         if(RQMTBA >= RQMTOE || bt < 0){
            bt = 0;
         }

         if(isPrimary()){
            setBatchTimeout(bt);            
            //System.out.println("RQMTBA = " + RQMTBA + "; RQMTOE = " + RQMTOE + "; BTIMEOUT = " + getBatchingTimeout() + "; BSIZE = " + getBatchSize() + "; rqsize = " + rqtable.size());
         }

   }

   public double computeRQMTBA(){
      double currentTime = getClockValue();
      if(RQCount > 0){
         return currentTime / RQCount;
      }else{
         return currentTime;
      }
   }
   
   public void updateRQInInformation(PBFTRequest r){
      /*increment the number of received requests */
      RQCount ++;
     if(!rqtable.containsKey(RQInfo.getEntry(r))){
         RQInfo info = new RQInfo(r);
         info.recvTime = getClockValue();         
         rqtable.put(info);
     }
   }


   @Override
   public void executeRequest(StatedPBFTRequestMessage loggedRequest, long currSEQ, int viewn) {
      
      super.executeRequest(loggedRequest, currSEQ, viewn);

      PBFTRequest r = loggedRequest.getRequest();
      RQInfo info = rqtable.get(RQInfo.getEntry(r));
      
      if(info != null && info.respTime < 0 && info.ordrTime > 0){
         info.respTime = getClockValue();

         double RQOED = (info.respTime - info.ordrTime);
         RQMTOE = RQMTOE * 0.9 + 0.1 * RQOED;
      }

      if(info != null){
         rqtable.remove(info.entry);
      }
   }

   @Override
   public void executeBatchCommand(PBFTPrePrepare preprepare) {
      super.executeBatchCommand(preprepare);
      
      int bs = 1;

      if(RQMTOE > 0){
         bs = (int)Math.ceil(RQMTOE/computeRQMTBA());
      }

      setBatchSize(bs);

   }


   /*=============== Utility classes ===============*/
   class RQTable extends Hashtable<String, RQInfo>{
      public RQInfo put(RQInfo info){
         return put(info.entry, info);
      }
   }
   
   static class RQInfo{
      double bits = 0.0;
      double recvTime = -1.0;
      double respTime = -1.0;
      double ordrTime = -1.0;
      String entry = "";
      RQInfo(PBFTRequest r){
         try{
           bits = XObject.objectToByteArray(r).length * 8;
           entry = getEntry(r);
         } catch (IOException ex) {
            Logger.getLogger(AutonomicPBFTServer.class.getName()).log(Level.SEVERE, null, ex);
         }
      }

      static String getEntry(PBFTRequest r){
         return getEntry(r.getClientID(), r.getTimestamp());
      }

      static String getEntry(PBFTReply r){
         return getEntry(r.getClientID(), r.getTimestamp());
      }

      static String getEntry(Object client, long timestamp){
         return client.toString() + "." + timestamp;
      }
   }
//   protected TimeVarying1stOrderEMA EMAInFlowEstimator = new TimeVarying1stOrderEMA(1000, true);
//   protected TimeVarying1stOrderEMA EMAInBitsEstimator = new TimeVarying1stOrderEMA(1000, true);
//   protected TimeVarying1stOrderEMA EMARCEstimator     = new TimeVarying1stOrderEMA(1000, false);
//   protected TimeVarying1stOrderEMA EMAvarRCEstimator  = new TimeVarying1stOrderEMA(1000, false);
//   protected TimeVarying1stOrderEMA EMAInMinPPEstimator = new TimeVarying1stOrderEMA(300000, true);
//   protected TimeVarying1stOrderEMA EMAInMaxPPEstimator = new TimeVarying1stOrderEMA(300000, true);
//   protected TimeVarying1stOrderEMA EMAInMeanPPEstimator = new TimeVarying1stOrderEMA(1000, true);
//   protected double accInBits  = 0.0;
//   protected double meanInBits = -1.0;
//   protected double meanInFlow = -1.0;
//   protected double meanIATime = 0.0;
//   protected double oldTimePP = -1;
//   protected double oldTimeRC = -1;
//   protected double minPPDelay = -1;
//   protected double maxPPDelay = -1;
//   protected double meanPPDelay = -1;
//   protected double RC = 0.0;
//   protected double varRC = 0.0;
//   protected double oldRC = 0.0;
//   protected double bitsInSystem = 0.0;
//
//   @Override
//   public synchronized void handle(PBFTRequest r) {
//      doRQSensing(r);
//      super.handle(r);
//   }
//
//   double oldRQTime = 0.0;
//   double nRQ = 0;
//   double meanOETime = 0.0;
//   public void doRQSensing(PBFTRequest r){
//      try {
//         double curTime = getClockValue();
//         byte[] inBytes = XObject.objectToByteArray(r);
//         double inBits = inBytes.length * 8;
//
//         if(!rqlog.containsKey(RQEventInfo.getEntry(r))){
//            RQEventInfo rqinfo = new RQEventInfo(r.getClientID(), r.getTimestamp());
//            rqlog.put(rqinfo.getEntry(), rqinfo);
//            rqinfo.recvTime = curTime;
//
//         }
//
//         if(oldRQTime < 0){
//            oldRQTime = curTime;
//         }
//
//         if(oldRQTime == curTime){
//            accInBits += inBits;
//            return;
//         }
//
//         double currInBits = inBits + accInBits;
//         double inFlowBits = currInBits/(curTime - oldRQTime);
//         nRQ++;
//         meanInBits = EMAInBitsEstimator.estimate(currInBits, curTime);
//         meanInFlow = EMAInFlowEstimator.estimate(inFlowBits, curTime);
//         meanIATime = curTime/nRQ;
//
//         accInBits = 0.0;
//         oldRQTime = curTime;
//
//
//         if(minPPDelay >= 0.0  && isPrimary()){
//            long stBatchTimeout = (long)Math.ceil(minPPDelay * meanInBits);
//            if(meanIATime > minPPDelay){
//               stBatchTimeout = 0;
//            }
//            setBatchTimeout(stBatchTimeout);
//            System.out.println("curTime = " + curTime + "; iat = " + meanIATime + "; minPP = " + minPPDelay + "; btimeout = " + stBatchTimeout + "; meanInBits = " + meanInBits);
//
//         }
//
//
//      } catch (IOException ex) {
//         Logger.getLogger(AutonomicPBFTServer.class.getName()).log(Level.SEVERE, null, ex);
//      }
//   }
//
//
//   @Override
//   public void emitPrePrepare(PBFTPrePrepare pp) {
//
//      if(pp != null && pp.getSequenceNumber() != null && !pplog.containsKey(pp.getSequenceNumber())){
//         PPEventInfo info = new PPEventInfo(pp.getSequenceNumber(), getClockValue());
//         pplog.put(pp.getSequenceNumber(), info);
//            try{
//
//               byte[] b = XObject.objectToByteArray(pp);
//
//               info.bits = b.length * 8;
//
//               bitsInSystem += info.bits;
//
//               //System.out.println("bitsInSystem = " + bitsInSystem);
//            }catch(Exception e){
//
//            }
//
//
//      }
//
//      super.emitPrePrepare(pp);
//   }
//
//
//   @Override
//   public void executeBatchCommand(PBFTPrePrepare preprepare) {
//      if(isPrimary() && preprepare != null){
//
//         long currSEQ = preprepare.getSequenceNumber();
//
//         if(pplog.containsKey(currSEQ)){
//            PPEventInfo info = pplog.get(currSEQ);
//            long send = info.sendTime;
//            long recv = info.recvTime = getClockValue();
//            long delay = (recv - send);
//            if(info.bits > 0){
//               double d = delay / info.bits;
//               double curTime = getClockValue();
//               meanPPDelay = EMAInMeanPPEstimator.estimate(d, curTime);
//
//               if(minPPDelay < 0 || minPPDelay > d){
//                  minPPDelay = d;
//                  //EMAInMinPPEstimator.setMean(d, curTime);
//               }else{
//                  //minPPDelay = EMAInMinPPEstimator.estimate(d, curTime);
//               }
//
//               if(maxPPDelay < 0 || maxPPDelay < d){
//                  maxPPDelay = d;
//                  //EMAInMaxPPEstimator.setMean(d, curTime);
//               }else{
//                  //maxPPDelay = EMAInMaxPPEstimator.estimate(d, curTime);
//               }
//
//               if(maxPPDelay < meanPPDelay){
//                  maxPPDelay = meanPPDelay;
//                  //EMAInMaxPPEstimator.setMean(meanPPDelay, curTime);
//               }
//
//               bitsInSystem -= info.bits;
//
//               oldRC = RC;
//               double RC2 = 0.0;
//               if(minPPDelay != maxPPDelay){
//                  RC = (meanPPDelay - minPPDelay)/(maxPPDelay - minPPDelay);
//                  RC2 = (d - minPPDelay)/(maxPPDelay - minPPDelay);
//               }else{
//                  RC = 0.0;
//               }
//
//               if(curTime > oldTimeRC){
//                  varRC = (RC - oldRC)/(curTime - oldTimeRC);
//               }
//               oldTimeRC = curTime;
//
//               double meanVarRC = EMAvarRCEstimator.estimate(varRC, curTime);
//
//               double meanRC = EMARCEstimator.estimate(RC, curTime);
//
//               //System.out.println(curTime + ";" + minPPDelay + ";" + maxPPDelay + ";" + meanPPDelay + ";" + RC + "; RC2 = " + RC2 + ";" + meanRC + ";" + meanVarRC + ";" + bitsInSystem);
//
//            }
//         }
//      }
//
//      super.executeBatchCommand(preprepare);
//   }
//
//   @Override
//   public void executeRequest(StatedPBFTRequestMessage loggedRequest, long currSEQ, int viewn) {
//      super.executeRequest(loggedRequest, currSEQ, viewn);
//
//      if(rqlog.containsKey(RQEventInfo.getEntry(loggedRequest.getRequest()))){
//         RQEventInfo rqinfo = rqlog.get(RQEventInfo.getEntry(loggedRequest.getRequest()));
//         rqinfo.respTime = getClockValue();
//         double d = rqinfo.respTime - rqinfo.recvTime;
//         //System.out.println("Req Delay = " + d);
//      }
//
//   }
//
//
//
//   LogPPEventInfo pplog = new LogPPEventInfo();
//   LogRQEventInfo rqlog = new LogRQEventInfo();
//
//   static class RQEventInfo{
//      Object client;
//      long timestamp;
//      double recvTime;
//      double respTime;
//
//      public  RQEventInfo(Object client, long timestamp){
//         this.client = client;
//         this.timestamp = timestamp;
//      }
//
//      public String getEntry(){
//         return getEntry(client, timestamp);
//      }
//
//      public static String getEntry(PBFTRequest req){
//         return getEntry(req.getClientID(), req.getTimestamp());
//      }
//
//      public static String getEntry(PBFTReply rep){
//         return getEntry(rep.getClientID(), rep.getTimestamp());
//      }
//
//      public static String getEntry(Object client, long timestamp){
//         return client.toString() + "." + timestamp;
//      }
//
//      public boolean isSame(PBFTRequest req){
//         return getEntry(req).equals(getEntry());
//      }
//
//      public boolean isSame(PBFTReply rep){
//         return getEntry(rep).equals(getEntry());
//      }
//   }
//
//   class LogRQEventInfo extends Hashtable<String, RQEventInfo>{
//
//   }
//   class PPEventInfo{
//      Long sequence;
//      Long sendTime;
//      Long recvTime;
//      double bits = 0.0;
//
//      PPEventInfo(Long sequence, Long sendTime){
//         this.sequence = sequence;
//         this.sendTime = sendTime;
//      }
//   }
//
//   class LogPPEventInfo extends Hashtable<Long, PPEventInfo>{
//
//   }
}
