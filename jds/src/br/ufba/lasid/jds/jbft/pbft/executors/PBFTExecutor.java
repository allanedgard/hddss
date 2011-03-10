/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.jbft.pbft.PBFT;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTExecutor<T> extends Executor<T>{

     PBFT protocol = null;

    public void setProtocol(PBFT p){
        this.protocol =  p;
    }

    public PBFT getProtocol(){
        return this.protocol;
    }

}
