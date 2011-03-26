/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.cs.IClient;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public interface IPBFTClient{
    /**
     * Perform an asynchronous service call. The result is deliver to the
     * registered application using the method on deliver.
     * @param payload - the operation.
     */
    public void asyncCall(IPayload payload);

    public void setClient(IClient client);
    public void setRetransmissionTimeout(long timeout);
    public long getRetransmissionTimeout();
    public void accept(IPayload payload);
    public void doSchedule(PBFTRequest request);
    //public void revokeSchedule(long timeout);

    
}
