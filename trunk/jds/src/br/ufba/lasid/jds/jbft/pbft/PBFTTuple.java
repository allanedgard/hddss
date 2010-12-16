/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.comm.Message;

/**
 *
 * @author aliriosa
 */
public class PBFTTuple extends Message{

    @Override
    public synchronized boolean equals(Object o) {

        Message m = (Message) o;
        String himContent = "";
        
        for(Object item : m.values()){
            himContent += item.toString();
        }

        String myContent = "";

        for(Object item : this.values()){

            myContent += item.toString();
            
        }

        return himContent.equals(myContent);
        
    }

}
