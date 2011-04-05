/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.decision.voting.Quorum;
import br.ufba.lasid.jds.decision.voting.QuorumStore;
import br.ufba.lasid.jds.decision.voting.Quorumtable;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class PBFTStateLog extends Hashtable<Long, PBFTLogEntry>{
    
    private static final long serialVersionUID = 9080466116863750014L;

     QuorumStore<String> qstore = new QuorumStore<String>();
    
    private static final String PREPAREQUORUMTABLE = "__PREPAREQUORUMTABLE";
    private static final String COMMITQUORUMTABLE = "__COMMITQUORUMTABLE";
    private static final String CHECKPOINTQUORUMTABLE = "__CHECKPOINTQUORUMTABLE";
       
    private int lastChangeViewTimestamp = -1;

    private long cpLowWaterMark = -1;

    protected  long nextPrePrepareSEQ =  0L;
    protected  long nextPrepareSEQ    =  0L;
    protected  long nextCommitSEQ     =  0L;
    protected  long nextExecuteSEQ    =  0L;

    protected PBFTNewViewtable nvtable = new PBFTNewViewtable();

    public void updateNextPrePrepareSEQ(PBFTPrePrepare m){
        synchronized(this){
            if(m != null && m.getSequenceNumber() != null){
                long seqn = m.getSequenceNumber();
                if(seqn == nextPrePrepareSEQ){
                    nextPrePrepareSEQ++;
                }
            }
        }
    }

    public void updateNextPrepareSEQ(PBFTPrepare m){
        synchronized(this){
            if(m != null && m.getSequenceNumber() != null){
                long seqn = m.getSequenceNumber();
                if(seqn < nextPrePrepareSEQ && seqn == nextPrepareSEQ){
                    nextPrepareSEQ++;
                }
            }
        }
    }

    public void updateNextCommitSEQ(PBFTCommit m){
        synchronized(this){
            if(m != null && m.getSequenceNumber() != null){
                long seqn = m.getSequenceNumber();
                if(seqn < nextPrePrepareSEQ && seqn < nextPrepareSEQ && seqn == nextCommitSEQ){
                    nextCommitSEQ++;
                }
            }
        }
    }

    public void updateNextExecuteSEQ(Long theSEQ){
        synchronized(this){
            if(theSEQ != null){
                long seqn = theSEQ;
                if(seqn < nextPrePrepareSEQ && seqn < nextPrepareSEQ && seqn < nextCommitSEQ && seqn == nextExecuteSEQ){
                    nextExecuteSEQ++;
                }
            }
        }
    }

    public void setNextCommitSEQ(long nextCommitSEQ) { this.nextCommitSEQ = nextCommitSEQ;}
    public void setNextExecuteSEQ(long nextExecuteSEQ) { this.nextExecuteSEQ = nextExecuteSEQ;}
    public void setNextPrePrepareSEQ(long nextPrePrepareSEQ) { this.nextPrePrepareSEQ = nextPrePrepareSEQ;}
    public void setNextPrepareSEQ(long nextPrepareSEQ) {this.nextPrepareSEQ = nextPrepareSEQ;}

    public long getNextCommitSEQ() {return nextCommitSEQ;}
    public long getNextPrePrepareSEQ() {return nextPrePrepareSEQ;}
    public long getNextPrepareSEQ() {return nextPrepareSEQ;}
    public long getNextExecuteSEQ() {return nextExecuteSEQ;}

    protected int nextViewNumber = 1;
    public void setNextViewNumber(int viewn){ this.nextViewNumber = viewn;}
    public int getNextViewNumber(){return this.nextViewNumber;}



    public Quorum getPrepareQuorum(Long seqn){
        synchronized(this){
        
            PBFTLogEntry entry = get(seqn);

            if(entry != null){
                return entry.getPrepareQuorum();
            }
        }
        return null;
    }

    public Quorum getCommitQuorum(Long seqn){

        synchronized(this){
            PBFTLogEntry entry = get(seqn);

            if(entry != null){
                return entry.getCommitQuorum();
            }
        }
        return null;
    }

    public PBFTPrePrepare getPrePrepare(Long seqn){
        synchronized(this){
            PBFTLogEntry entry = get(seqn);

            if(entry != null){
                return entry.getPrePrepare();
            }
        }
        return null;

    }

    public void setCheckpointLowWaterMark(long mark){
        synchronized(this){
            if(cpLowWaterMark < mark)
                cpLowWaterMark = mark;
        }
    }
    
    public long getCheckpointLowWaterMark(){
        return cpLowWaterMark;
    }

    public long getCheckpointHighWaterMark(long checkpointPeriod, long factor){
            return cpLowWaterMark + factor * checkpointPeriod;
    }


    public Quorum getQuorum(String qtname, String qname){

        Quorumtable<String> qtable = getQuorumTable(qtname);
        Quorum quorum = qtable.get(qname);
        
        return quorum;
    }

    public Quorumtable<String> getQuorumTable(String name){

        synchronized(this){
            Quorumtable<String> qtable = qstore.get(name);

            if(qtable == null){

                qtable = new Quorumtable<String>();
                qstore.put(name, qtable);
            }

            return qtable;
        }
        
    }
    

    public PBFTNewViewtable getNewViewTable(){
       return nvtable;
    }
    
    public void setLastChangeViewTimestamp(int lastChangeViewTimestamp) {
        this.lastChangeViewTimestamp = lastChangeViewTimestamp;
    }


    public int getLastChangeViewTimestamp() {
        return lastChangeViewTimestamp;
    }

    public void garbage(long seqn) {
        synchronized(this){
            long finalSEQ = seqn; 

            ArrayList<Long> seqns = new ArrayList<Long>();
            seqns.addAll(keySet());

            Collections.sort(seqns);

            if(!seqns.isEmpty()){

                long startSEQ = seqns.get(0);
                Quorumtable qtable = null;

                for(long seq = startSEQ; seq <= finalSEQ; seq++){
                    qtable = qstore.get(PREPAREQUORUMTABLE);    if(qtable != null) qtable.remove(seq);
                    qtable = qstore.get(COMMITQUORUMTABLE);     if(qtable != null) qtable.remove(seq);
                    qtable = qstore.get(CHECKPOINTQUORUMTABLE); if(qtable != null) qtable.remove(seq);

                    remove(seq);

                }
            }
        }
    }

}
