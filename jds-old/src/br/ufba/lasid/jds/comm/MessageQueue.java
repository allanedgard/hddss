/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

/**
 *
 * @author aliriosa
 */
public class MessageQueue extends MessageCollection{

    public void enqueue(IMessage m){
       if(!contains(m)){
         add(m);
       }
    }

    public IMessage remove(){
        IMessage m = null;
        if(!isEmpty()){
            m = get(0);
            remove(0);
        }

        return m;
    }

}
