/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.DigestList;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestMessageList;

/**
 *
 * @author aliriosa
 */
public class PBFTBatchMessage extends PBFTMessage{

    protected PBFTRequestMessageList requests = new PBFTRequestMessageList();
    protected DigestList digests = new DigestList();
    
    protected int maxSize = 0;
    protected int forcedcomplete = -1;
    protected Long timestamp = new Long(0);

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    
//    public Object getSourceID(){
//        return getSourceID().getID();
//    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean complete(){
        return complete(maxSize);
    }

    public boolean complete(int maxSize){
        return maxSize <= requests.size() || forcedcomplete == 1;
    }

    public void setComplete(){
        maxSize = requests.size();
        forcedcomplete = 1;
    }

    public PBFTRequestMessageList getRequests(){
        return requests;
    }

    /**
     * Add a request to request list of the batch if it has not been added yet.
     * @param request -- the request.
     */
    public void addRequest(PBFTRequest request){

        /**
         * If the request has not been added yet then it will be added to the
         * request list of the batch.
         */
        if(request != null && !hasRequest(request)){
            
            PBFTRequest m = null; //new PBFTRequest();
            //m.putAll(m);
            
            requests.add(m);
            
        }
    }

    /**
     * Check if a batch has client requests.
     * @return -- true if the batch has requests.
     */
    public boolean hasRequests(){
        return requests.size() > 0;
    }
    
    /**
     * Check if a request has been added to the request list of the batch.
     * @param request -- the request.
     * @return -- true if the request was added.
     */
    public boolean hasRequest(PBFTRequest request){

        /**
         * If request is null the it wasn't added.
         */

        if(request == null){

            return false;
            
        }

        /**
         * for each request in request list, if the inputed request is equal to
         * a previously existant request then it was added.
         */

        return requests.contains(request);
        
    }


    @Override
    public final synchronized String toString() {

        return (
                "<BATCH"                            + ","  +
                 "SERVER = " + getSourceID()        + ", " +
                 "SIZE = "   + getRequests().size() + ", " +
                 "TIMESTAMP = " + getTimestamp()    +
                 ">"
        );
    }

    Object sourceID;

    public Object getSourceID() {
        return sourceID;
    }

    public void setSourceID(Object sourceID) {
        this.sourceID = sourceID;
    }
    
    
}
