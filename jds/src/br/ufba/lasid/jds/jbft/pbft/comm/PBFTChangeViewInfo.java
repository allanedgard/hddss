/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.comm.IMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeViewInfo{
   TreeMap<Integer, ChangeViewEntryList> table = new TreeMap<Integer, ChangeViewEntryList>();
   int minView = 0;
   Object ownerID;
   
   public PBFTChangeViewInfo(Object ownerID ) {
      this.ownerID = ownerID;
   }

   public boolean gc(int view){
      boolean ok = false;
      if(!table.isEmpty()){
         int start = table.firstKey();

         for(int lookup = start; lookup <= view; lookup++){
            table.remove(lookup);
            ok = true;
         }

      }
      return ok;
   }
                            
   public PBFTChangeViewACK buildMyACK(PBFTChangeView cv, String digest){
      ChangeViewEntry entry = get(cv);
      if(entry != null && entry.changeViewACK == null){
         PBFTChangeViewACK ack = new PBFTChangeViewACK(cv.getViewNumber(), ownerID, cv.getReplicaID(), digest);
         entry.digest = digest;
         entry.changeViewACK = ack;
         return ack;
      }

      return null;
   }

   public PBFTChangeViewACK getMyACK(int view, Object prompterID){
      if(!table.isEmpty()){
         if(table.containsKey(view)){
            ChangeViewEntryList entries = table.get(view);
            for(ChangeViewEntry entry : entries){
               if(entry.changeView.getReplicaID().equals(prompterID)){
                  return entry.changeViewACK;
               }
            }
         }
      }
      return null;
   }
   public List<PBFTChangeView> getPendingACK(int view){
      ArrayList<PBFTChangeView> pending = new ArrayList<PBFTChangeView>();
      if(!table.isEmpty()){
         if(table.containsKey(view)){
            ChangeViewEntryList list = table.get(view);
            for(ChangeViewEntry entry : list){
               if(entry.changeViewACK  == null){
                  pending.add(entry.changeView);
               }
            }
         }
      }
      return pending;
   }

   public PBFTChangeView getChangeView(PBFTChangeViewACK ack){
      if(ack != null && ack.getPrompterID() != null && !table.isEmpty()){

         int          ackView = ack.getViewNumber();
         Object ackPrompterID = ack.getPrompterID();
         Object  ackReplicaID = ack.getReplicaID();
         String     ackDigest = ack.getDigest();

         if(!table.containsKey(ackView)){
            return null;
         }
         
         ChangeViewEntryList msgs = table.get(ackView);

         for(ChangeViewEntry m : msgs){
            if(m.changeViewACK != null){
               PBFTChangeViewACK myack = m.changeViewACK;
               if(myack!=null && myack.getDigest().equals(ackDigest) && myack.getPrompterID().equals(ackPrompterID) && myack.getReplicaID().equals(ackReplicaID)){
                  return m.changeView;
               }
            }
         }

      }
      return null;
   }
   
   public boolean put(PBFTChangeView cv){
      if(isValid(cv)){
         if(!contains(cv)){
            int view = cv.getViewNumber();
            ChangeViewEntryList msgs = table.get(view);

            if(msgs == null){
               msgs = new ChangeViewEntryList();
               table.put(view, msgs);
            }

            msgs.add(new ChangeViewEntry(cv, null, null));

            return true;
         }
      }
      return false;
   }

   public void setMinimumViewNumber(int minView){
      this.minView = minView;
   }

   public boolean contains(PBFTChangeView cv){
      return get(cv) != null;
   }

   private ChangeViewEntry get(PBFTChangeView cv){

      if(isValid(cv) && !table.isEmpty()){

         int        cvView = cv.getViewNumber();
         long       cvLCWM = cv.getLowCheckpointWaterMark();
         Object cvSenderID = cv.getReplicaID();
         
         if(!table.containsKey(cvView)){
            return null;
         }

         ChangeViewEntryList msgs = table.get(cvView);

         for(ChangeViewEntry m : msgs){
            PBFTChangeView me = m.changeView;

            int        meView = me.getViewNumber();
            long       meLCWM = me.getLowCheckpointWaterMark();
            Object meSenderID = me.getReplicaID();

            if(cvView == meView && cvLCWM == meLCWM && cvSenderID.equals(meSenderID)){
               return m;
            }

         }

      }
      return null;
   }


   public PBFTChangeView getChangeView(int view, Object prompterID){
      if(table.isEmpty()){
         return null;
      }

      if(!table.containsKey(view)){
         return null;
      }

      ChangeViewEntryList list = table.get(view);

      if(!(list != null && !list.isEmpty())){
         return null;
      }

      for(ChangeViewEntry entry : list){
         if(entry.changeView != null && entry.changeView.getReplicaID().equals(prompterID)){
            return entry.changeView;
         }
      }
      return null;
   }
   
   public boolean isValid(PBFTChangeView cv){
      if(!(
        cv != null && cv.getViewNumber() != null && cv.getCheckpointSet() != null && cv.getPrepareSet() != null &&
        cv.getPrePrepareSet() != null && cv.getReplicaID() != null
      )){
         return false;
      }

      for(IMessage m : cv.getPrePrepareSet()){

         PBFTPrePrepare pp = (PBFTPrePrepare) m;

         if(pp.getDigests() == null){
            return false;
         }

         if(pp.getSequenceNumber() == null){
            return false;
         }

         if(pp.getViewNumber() == null){
            return false;
         }

         int viewn = pp.getViewNumber();

         if(viewn < minView){
            return false;
         }

      }

      for(IMessage m : cv.getPrepareSet()){

         PBFTPrepare p = (PBFTPrepare) m;

         if(p.getDigests() == null){
            return false;
         }

         
         if(p.getSequenceNumber() == null){
            return false;
         }

         if(p.getViewNumber() == null){
            return false;
         }

         int viewn = p.getViewNumber();

         if(viewn < minView){
            return false;
         }

      }

      
      return true;
   }


   public int count(int view){
      if(!table.isEmpty()){
         if(table.containsKey(view)){
            ChangeViewEntryList msgs = table.get(view);
            return msgs.size();
         }

      }
      return 0;

   }

   public boolean hasAtLeast(int n, int view){
      return count(view) >= n;
   }

   public int biggestWithAtLeast(int n){
      if(!table.isEmpty()){
         int lview = table.lastKey();
         int fview = table.firstKey();

         for(int view = lview; view >= fview; view--){
               if(table.containsKey(view)){
                  if(hasAtLeast(n, view)){
                     return view;
                  }
               }
         }
      }
      return -1;
   }

   public int biggestWithExactly(int n){
      if(!table.isEmpty()){
         int lview = table.lastKey();
         int fview = table.firstKey();

         for(int view = lview; view >= fview; view--){
               if(table.containsKey(view)){
                  if(hasExactly(n, view)){
                     return view;
                  }
               }
         }
      }
      return -1;
   }

   public int biggestWithMoreThan(int n){
      if(!table.isEmpty()){
         int lview = table.lastKey();
         int fview = table.firstKey();

         for(int view = lview; view >= fview; view--){
               if(table.containsKey(view)){
                  if(hasMoreThan(n, view)){
                     return view;
                  }
               }
         }
      }
      return -1;
   }

   public boolean hasExactly(int n, int view){
      return count(view) == n;
   }

   public boolean hasMoreThan(int n, int view){
      return count(view) > n;
   }

   public boolean isValid(PBFTChangeViewACK ack){
      if(!(ack != null && ack.getPrompterID() != null && ack.getReplicaID() != null && ack.getDigest() != null)){
         return false;
      }
      return true;
   }
   
   class ChangeViewEntry {
      PBFTChangeView changeView;
      PBFTChangeViewACK changeViewACK;
      String digest;

      public ChangeViewEntry(PBFTChangeView changeview, PBFTChangeViewACK changeviewACK, String digest) {
         this.changeView = changeview;
         this.changeViewACK = changeviewACK;
         this.digest = digest;

      }
      
   }

   class ChangeViewEntryList extends ArrayList<ChangeViewEntry>{
      
   }
}
