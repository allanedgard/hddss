/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class Quorum extends ArrayList<IMessage>{
    
    private static final long serialVersionUID = -1024102331698905981L;

    int quorumSize = 1;
    
    public Quorum(){
        super();
    }

    public Quorum(int quorumSize){
        this.quorumSize = quorumSize;
    }

    public boolean complete(){
        return size() >= quorumSize;
    }

    @Override
    public String toString() {
        String tostr =  "Quorum{" + "quorumSize=" + quorumSize + "}:";
        for(IMessage m : this){
            tostr += "\n" +m;
        }
        return tostr;
    }
}
