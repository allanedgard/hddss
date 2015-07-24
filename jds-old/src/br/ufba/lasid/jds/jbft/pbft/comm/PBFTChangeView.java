/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageCollection;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.util.DigestList;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeView extends PBFTServerMessage{

    MessageCollection preprepareset = new MessageCollection();
    MessageCollection prepareset = new MessageCollection();
    MessageCollection checkpointset = new MessageCollection();

    public PBFTChangeView(long lcwm, int newveiw, Object rid){
        setLowCheckpointWaterMark(lcwm);
        setViewNumber(newveiw);
        setReplicaID(rid);
    }
    
    public MessageCollection getPrePrepareSet(){
        return preprepareset;
    }

    public MessageCollection getPrepareSet(){
        return prepareset;
    }

    public MessageCollection getCheckpointSet(){
        return checkpointset;
    }

    public void addPrePrepare(long seqn, DigestList digests, int viewn){
        for(int i = preprepareset.size()-1 ;  i >= 0; i--){
            PBFTPrePrepare pp = (PBFTPrePrepare) preprepareset.get(i);
            long seqn1 = pp.getSequenceNumber();
            if(seqn == seqn1){
                preprepareset.remove(i);
            }
        }
        
        PBFTPrePrepare pp = new PBFTPrePrepare(viewn, seqn, null);
        pp.digests.addAll(digests);
        preprepareset.add(pp);
    }

    public void addPrepare(long seqn, DigestList digests, int viewn){
        for(int i = prepareset.size()-1 ;  i >= 0; i--){
            PBFTPrepare p = (PBFTPrepare) prepareset.get(i);
            long seqn1 = p.getSequenceNumber();
            if(seqn == seqn1){
                prepareset.remove(i);
            }
        }

        PBFTPrepare p = new PBFTPrepare(viewn, seqn, null);
        p.digests.addAll(digests);
        prepareset.add(p);
    }


    public void addCheckpoint(long seqn, String digest){
        for(int i = checkpointset.size()-1 ;  i >= 0; i--){
            PBFTCheckpoint c = (PBFTCheckpoint) checkpointset.get(i);
            long seqn1 = c.getSequenceNumber();
            if(seqn == seqn1){
                checkpointset.remove(i);
            }
        }

        PBFTCheckpoint c = new PBFTCheckpoint(seqn, digest, null);
        checkpointset.add(c);
        
    }
    public void setLowCheckpointWaterMark(long seqn){
        setSequenceNumber(seqn);
    }

    public long getLowCheckpointWaterMark(){
        return getSequenceNumber();
    }
    
    private String mc2str(MessageCollection mc){
        String str = ""; String more = "";
        for(IMessage m : mc){
            str += more + m;
            more = ",";
        }

        return str;
    }
    @Override
    public final String toString() {
        Object rid = getReplicaID();
        String mcpp = mc2str(preprepareset);
        String mcpr = mc2str(prepareset);
        String mcck = mc2str(checkpointset);
        
        return (
            "<VIEW-CHANGE" + ", " +
             "VIEW = " + getViewNumber() + ", " +
             "SEQ = " + getSequenceNumber() + ", " +
             "CHECKPONTSET = " + mcck + ", " +
             "PREPARESET = " + mcpr + ", " +
             "PREPREPARESET = " + mcpp + ", " +
             "SENDER = " + (rid == null ? "NULL" : rid) +
             ">"
        );
    }

   public int getTAG() {
      return IPBFTServer.CHANGEVIEW;
   }

   public String getTAGString() {
      return "CHANGEVIEW";
   }

}
