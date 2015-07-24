/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.voting.Quorum;
import br.ufba.lasid.jds.decision.voting.Quorumtable;
import br.ufba.lasid.jds.decision.voting.SoftQuorum;
import br.ufba.lasid.jds.group.decision.Vote;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewACK;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.server.decision.PrePrepareSubject;
import br.ufba.lasid.jds.jbft.pbft.server.decision.PrepareSubject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 *
 * @author aliriosa
 */
public class PBFTNewViewConstructor {

   private Hashtable<Integer, ChangeViewtable> cvdatabase = new Hashtable<Integer, ChangeViewtable>();
   private CheckpointDatabase chdatabase = new CheckpointDatabase();
   
   protected Quorumtable<Long> ppqtable = new Quorumtable<Long>();
   protected Quorumtable<Long> prqtable = new Quorumtable<Long>();
   protected IPBFTServer pbft;

   protected long     LCWM;
   protected String DIGEST;

   public PBFTNewViewConstructor(IPBFTServer pbft, long checkpointFactor, long checkpointPeriod, int resilience) {
      this.pbft = pbft;
      this.checkpointFactor = checkpointFactor;
      this.checkpointPeriod = checkpointPeriod;
      this.resilience = resilience;
   }

   public void gc(int view){
      if(!cvdatabase.isEmpty()){
         if(cvdatabase.containsKey(view)){
            ArrayList<Integer> views = new ArrayList<Integer>(cvdatabase.keySet());
            for(Integer lookup : views){
               if(lookup <= view){
                  cvdatabase.remove(lookup);
                  chdatabase.remove(lookup);                  
               }
            }
         }
      }
   }


   public boolean isValid(PBFTChangeView cv){
      if(!(cv!=null && cv.getCheckpointSet()!=null && cv.getPrePrepareSet()!=null && cv.getPrepareSet()!=null && cv.getReplicaID()!=null && cv.getViewNumber()!=null)){
         return false;
      }
      return true;
   }

   public boolean hasAtLeastMessages(int view, int n){
      return count(view) >= n;
   }

   public int count(int view){
      int count = 0;

      if(!cvdatabase.isEmpty()){
         if(cvdatabase.containsKey(view)){
            count = cvdatabase.get(view).size();
         }
      }

      return count;
      
   }

   public boolean addChangeViewACK(PBFTChangeViewACK ack){
      if(!(ack != null && ack.getDigest() != null && ack.getPrompterID() != null && ack.getReplicaID() != null)){
         return false;
      }

      if(!cvdatabase.isEmpty()){
         int view = ack.getViewNumber();
         if(cvdatabase.containsKey(view)){
            ChangeViewtable table = cvdatabase.get(view);
            if(table != null && !table.isEmpty()){
               if(table.containsKey(ack.getPrompterID())){
                  ChangeViewEntry entry = table.get(ack.getPrompterID());
                  entry.changeViewACK = ack;
                  return true;
               }
            }
         }
      }
      return false;
   }
   
   public boolean addChangeView(PBFTChangeView cv){

      if(isValid(cv)){
         
         int view = cv.getViewNumber();
         ChangeViewtable table = cvdatabase.get(view);

         if(table == null){
            table = new ChangeViewtable();
            cvdatabase.put(view, table);
         }
         
         table.put(cv.getReplicaID(), new ChangeViewEntry(cv, null));
         
         for(IMessage im : cv.getCheckpointSet()){
            PBFTCheckpoint checkpoint = (PBFTCheckpoint) im;
            chdatabase.put(view, cv.getReplicaID(), checkpoint);            
         }

         int f = resilience;
         
         for(IMessage im : cv.getPrePrepareSet()){

            PBFTPrePrepare pp = (PBFTPrePrepare) im;
            long seqn = pp.getSequenceNumber();
            
            //pp.setReplicaID(cv.getReplicaID());

            Quorum q = ppqtable.get(seqn);

            if(q == null){
               q = new SoftQuorum(f + 1);
               ppqtable.put(seqn, q);
            }

            q.add(new Vote(cv.getReplicaID(), new PrePrepareSubject(pp) ));
         }

         for(IMessage im : cv.getPrepareSet()){

            PBFTPrepare pr = (PBFTPrepare) im;
            long seqn = pr.getSequenceNumber();

            //pr.setReplicaID(cv.getReplicaID());

            Quorum q = prqtable.get(seqn);

            if(q == null){
               q = new SoftQuorum(2 * f + 1);
               prqtable.put(seqn, q);
            }

            q.add(new Vote(cv.getReplicaID(), new PrepareSubject(pr) ));
            
         }
         
         return true;
      }

      return false;
   }

   public Long computeLCWM(int view){
      return computeLCWM(view, checkpointQuorum);
   }
   public Long computeLCWM(int view, int n){
      
      if(!chdatabase.isEmpty()){
         if(chdatabase.containsKey(view)){
            return chdatabase.computeLCWM(view, n);
         }
      }

      return null;
   }

   public int size(){
      return cvdatabase.size();
   }

   public void clear(){
      cvdatabase.clear();
   }
   protected long checkpointPeriod = 0;
   protected long checkpointFactor = 0;
   protected int resilience = 0;

   protected int checkpointQuorum = 0;

   public void setCheckpointQuorumSize(int n){
      checkpointQuorum = n;
   }
   

   public PBFTNewView buildNewView(int view){
      if(cvdatabase.isEmpty()){
         return null;
      }

      if(!cvdatabase.containsKey(view)){
         return null;
      }


      Long lcwm = computeLCWM(view);
      
      if(lcwm == null){
         return null;
      }

      long hcwm = lcwm + checkpointFactor * checkpointPeriod;


      PBFTNewView nv = new PBFTNewView();

      nv.setViewNumber(view);
      
      long minSEQ = hcwm;
      long maxSEQ = lcwm;

      if(!prqtable.isEmpty()){
         long fseqn = prqtable.firstKey();
         long lseqn = prqtable.lastKey();

         for(long seqn = fseqn; seqn <= lseqn; seqn++){
            Quorum q = prqtable.get(seqn);
            ISubject decision = null;

            if(q != null) decision = q.decide();

            if(decision != null && seqn <= hcwm && seqn > maxSEQ) maxSEQ = seqn;
            if(decision != null && seqn >  lcwm && seqn < minSEQ) minSEQ = seqn;
         }         
      }

      if(!ppqtable.isEmpty()){
         long fseqn = ppqtable.firstKey();
         long lseqn = ppqtable.lastKey();

         for(long seqn = fseqn; seqn <= lseqn; seqn++){
            Quorum q = ppqtable.get(seqn);
            ISubject decision = null;

            if(q != null) decision = q.decide();

            if(decision != null && seqn <= hcwm && seqn > maxSEQ) maxSEQ = lseqn;
            if(decision != null && seqn >  lcwm && seqn < minSEQ) minSEQ = seqn;
         }
      }

      for(long seqn = minSEQ; seqn <= maxSEQ; seqn ++){
         Quorum ppq = ppqtable.get(seqn);
         Quorum prq = prqtable.get(seqn);

         if(ppq == null || prq == null){
            continue;
         }

         PrePrepareSubject ppd = (PrePrepareSubject) ppq.decide();
         PrepareSubject    prd = (  PrepareSubject ) prq.decide();


         /*if both quorums were decided then I'll be able to decide by such pre-prepare message*/
         if(ppd != null && prd != null){
            PBFTPrePrepare pp = new PBFTPrePrepare(view, seqn, pbft.getLocalServerID());
            pp.getDigests().addAll(prd.getPrepare().getDigests());
            nv.getPrePrepareSet().add(pp);
         }

      }

      ChangeViewtable table = cvdatabase.get(view);

      if(table != null && !table.isEmpty()){
         for(Object rpid : table.keySet()){
            ChangeViewEntry entry = table.get(rpid);
            nv.getChangeViewtable().put(rpid, entry.changeViewACK.getDigest());
         }
      }

      nv.setSequenceNumber(LCWM);
      nv.setDigest(DIGEST);
      
      return nv;
   }


   class ChangeViewtable extends Hashtable<Object, ChangeViewEntry> {
   }

   class ChangeViewEntry{
      PBFTChangeView changeView;
      PBFTChangeViewACK changeViewACK;

      public ChangeViewEntry(PBFTChangeView changeView, PBFTChangeViewACK changeViewACK) {
         this.changeView = changeView;
         this.changeViewACK = changeViewACK;
      }
      
   }

   class CheckpointDatabase extends Hashtable<Integer, Checkpointtable>{

      public boolean put(int view, Object replicaID, PBFTCheckpoint checkpoint){
         if( !(replicaID != null && view >= 0 && checkpoint != null && checkpoint.getSequenceNumber() != null && checkpoint.getDigest() != null)){
            return false;
         }
         
         Checkpointtable table = get(view);

         if(table == null){
            table = new Checkpointtable();
            put(view, table);
         }

         return table.put(replicaID, checkpoint);
      }

      public Long computeLCWM(int view, int n){
         if(!isEmpty()){
            if(containsKey(view)){
               Checkpointtable ctable = get(view);
               return ctable.computeLCWM(n);
            }
         }

         return null;
      }
   }

   class Checkpointtable extends TreeMap<Long, CheckpointEntry>{

      public boolean put(Object replicaID, PBFTCheckpoint checkpoint){

         CheckpointEntry entry = get(checkpoint.getSequenceNumber());

         if(entry == null){
            entry = new CheckpointEntry();
            put(checkpoint.getSequenceNumber(), entry);
         }

         return entry.put(replicaID, checkpoint);
         
      }

      public Long computeLCWM(int n){
         if(!isEmpty()){
            long fseqn = firstKey();
            long lseqn = lastKey();

            for(Long seqn = lseqn; seqn >= fseqn; seqn --){
               CheckpointEntry entry = get(seqn);

               if(entry != null && !entry.isEmpty()){
                  for(String digest : entry.keySet()){
                     CheckpointList list = entry.get(digest);
                     if(list != null && list.size() >= n){
                        LCWM   = seqn;
                        DIGEST = digest;
                        return seqn;
                     }
                  }
               }
            }
         }

         return null;
         
      }
   }

   class CheckpointEntry extends Hashtable<String, CheckpointList>{

      public boolean put(Object replicaID, PBFTCheckpoint checkpoint){

         CheckpointList list = get(checkpoint.getDigest());

         if(list == null){
            list = new CheckpointList();
            put(checkpoint.getDigest(), list);
         }

         list.put(replicaID, checkpoint);
         
         return true;
      }
      
   }
                                         /*replicaID, PBFTCheckpoint*/
   class CheckpointList extends Hashtable<Object, PBFTCheckpoint>{
      
   }
}
