/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.util.DigestList;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTMessageOrdering extends PBFTServerMessage{

   protected DigestList digests = new DigestList();

    public DigestList getDigests() {
        return digests;
    }

    public String digestsToString(){
        String str  = "";
        String more = "";
        for(String s : digests){
            str += more + s;
            more = "; ";
        }

        return str;
    }

}
