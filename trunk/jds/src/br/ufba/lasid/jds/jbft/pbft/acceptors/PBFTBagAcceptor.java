/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.acceptors;

import br.ufba.lasid.hdf.ISupplier;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;

/**
 *
 * @author aliriosa
 */
public class PBFTBagAcceptor extends PBFTAcceptor<PBFTBag>{

    PBFTPrePrepareAcceptor aPrePrepareAcceptor;
    PBFTPrepareAcceptor aPrepareAcceptor;
    PBFTCommitAcceptor aCommitAcceptor;
    PBFTCheckpointAcceptor aCheckpointAcceptor;
    public PBFTBagAcceptor(PBFT protocol) {
        super(protocol);
        aPrePrepareAcceptor = new PBFTPrePrepareAcceptor(protocol);
        aPrepareAcceptor = new PBFTPrepareAcceptor(protocol);
        aCommitAcceptor = new PBFTCommitAcceptor(protocol);
        aCheckpointAcceptor = new PBFTCheckpointAcceptor(protocol);
    }

    public void setSupplier(ISupplier supplier){
        aCommitAcceptor.setSupplier(supplier);

    }
    
    public synchronized boolean accept(PBFTBag bag) {
        PBFTServer pbft = (PBFTServer) getProtocol();

        for(IMessage m : bag.getMessages()){

            if(m instanceof PBFTPrePrepare) {
                aPrePrepareAcceptor.accept((PBFTPrePrepare)m);
            }

            if(m instanceof PBFTPrepare) {
                aPrepareAcceptor.accept((PBFTPrepare)m);
            }

            if(m instanceof PBFTCommit) {
                PBFTCommit commit = (PBFTCommit)m;
                aCommitAcceptor.accept(commit);
            }

            if(m instanceof PBFTCheckpoint){
                aCheckpointAcceptor.accept((PBFTCheckpoint)m);
            }
        }

        return true;
    }

    

}
