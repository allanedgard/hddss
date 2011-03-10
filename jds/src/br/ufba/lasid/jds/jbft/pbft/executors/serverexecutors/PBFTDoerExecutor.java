/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.acceptors.PBFTDoerAcceptor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCollectorServant;

/**
 *
 * @author aliriosa
 */
public class PBFTDoerExecutor extends PBFTCollectorServant<PBFTCommit>{

    PBFTDoerAcceptor acceptor;
    public PBFTDoerExecutor(){

    }

    public PBFTDoerExecutor(PBFT p){
        setProtocol(p);
        acceptor = new PBFTDoerAcceptor(p);
    }

    protected synchronized boolean accept(PBFTCommit commit){

        if(commit != null){
            return acceptor.accept(commit.getSequenceNumber());
        }

        return false;
    }

    public boolean canConsume(Object object) {
        return (object instanceof PBFTCommit);
    }

    @Override
    public void execute(){

        while(true){
            try{

                PBFTCommit m = (PBFTCommit)getInbox().remove();

                accept(m);
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
    }
}

