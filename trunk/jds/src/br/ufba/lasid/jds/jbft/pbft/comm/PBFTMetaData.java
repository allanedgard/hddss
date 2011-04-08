/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.ft.util.PartList;
import br.ufba.lasid.jds.ft.util.PartTree.PartEntry;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;

/**
 *
 * @author aliriosa
 */
public class PBFTMetaData extends PBFTServerMessage{

    //CheckpointIndexList indexes = new CheckpointIndexList();
    PartList subparts = new PartList();
    long ipart = -1L;
    long lpart = -1L;

    public PBFTMetaData(Long chekpointSEQ, Long lpart, Long ipart, Object replicaID) {
        setPartLevel(lpart);
        setPartIndex(ipart);
        setSequenceNumber(chekpointSEQ);
        setReplicaID(replicaID);
    }

    public PBFTMetaData(Long seqn, Object replicaID) {
        setSequenceNumber(seqn);
        setReplicaID(replicaID);
    }

    public PartList getSubparts() {
        return subparts;
    }

    public void setSubparts(PartList subparts) {
        this.subparts.clear();
        this.subparts.addAll(subparts);
    }

    public long getPartIndex() {
        return ipart;
    }

    public void setPartIndex(long ipart) {
        this.ipart = ipart;
    }

    public long getPartLevel() {
        return lpart;
    }

    public void setPartLevel(long lpart) {
        this.lpart = lpart;
    }

    public long getCheckpoint(){
        return this.getSequenceNumber();
    }

    public void setCheckpoint(Long checkpointSEQ){
        this.setSequenceNumber(checkpointSEQ);
    }



    

////    public CheckpointIndexList getIndexes() {
////        return indexes;
////    }
////
////    public void addIndex(CheckpointIndex _index){
////        if(_index == null){
////            return;
////        }
////        for(CheckpointIndex index : indexes){
////            if(index.getIndexPattern().equals(_index.getIndexPattern())){
////                return;
////            }
////        }
////
////        indexes.add(_index);
////
////    }
//    public void addIndex(String pattern){
//        addIndex(new CheckpointIndex(pattern));
//    }
//
//    public void addIndex(String digest, long seqn){
//        addIndex(new CheckpointIndex(sequenceNumber, digest));
//    }
//
    @Override
    public final String toString() {

        String ssubparts = ""; String more  = "";
        
        for(PartEntry entry : subparts){
            String ssubpart = 
                    "<SUBPART" + ", " +
                       "L = " + entry.getPartLevel() + ", " +
                       "X = " + entry.getPartIndex() + ", " +
                       "LM =" + entry.getPartCheckpoint() + ", " +
                       "D = " + entry.getDigest() +
                     ">";
            ssubparts += more + ssubpart;
            more = ";";
        }
        
        return (
                "<META-DATA" + ", " +
                 "PARTCHECKPOINT = " + getCheckpoint() + ", " +
                 "PARTLEVEL = " + getPartLevel() + ", " +
                 "PARTINDEX = " + getPartIndex() + ", " +
                 "SUBPARTS  = {" + ssubparts + "}, " +
                 "SENDER = " + getReplicaID() +
                 ">"
        );
    }

   public int getTAG() {
      return IPBFTServer.METADATA;
   }

   public String getTAGString() {
      return "METADATA";
   }
}
