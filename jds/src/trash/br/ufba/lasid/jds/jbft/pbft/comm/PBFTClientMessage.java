/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTClientMessage extends PBFTMessage{
    Object clientID;
    Long timestamp;
    public void setClientID(Object ID){

        clientID = ID;

    }

    public Object getClientID(){
        return clientID;//get(PBFTMessage.CLIENTIDFIELD);
    }

    public void setTimestamp(Long t){
        timestamp = t;//put(PBFTMessage.TIMESTAMPFIELD, t);
    }

    public Long getTimestamp(){
        return timestamp;//(Long)get(PBFTMessage.TIMESTAMPFIELD);
    }

}
