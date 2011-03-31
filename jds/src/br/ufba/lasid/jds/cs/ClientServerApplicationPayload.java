/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs;

import br.ufba.lasid.jds.util.IPayload;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class ClientServerApplicationPayload extends Hashtable<String, Object> implements IPayload{

    public Object getContent() {
        return this;
    }

    public void setContent(Object content) {
        //do nothing
    }

   public int getSizeInBytes() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

}
