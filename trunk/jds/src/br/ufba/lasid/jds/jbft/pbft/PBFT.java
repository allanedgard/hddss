/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.group.IGroup;
import trash.br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.architectures.Architecture;
import br.ufba.lasid.jds.security.IMessageAuthenticator;
import br.ufba.lasid.jds.util.IClock;
import br.ufba.lasid.jds.util.IScheduler;
import br.ufba.lasid.jds.util.TaskTable;
import br.ufba.lasid.jds.util.TaskTableStore;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTStateLog;

/**
 *
 * @author aliriosa
 */
public class PBFT extends DistributedProtocol{

    protected TaskTableStore ttstore = new TaskTableStore();

    public static String REQUESTTASKS       = "REQUESTTASKS";
    public static String VIEWCHANGETASKS    = "VIEWCHANGETASKS";
    public static String BATCHTASKS         = "BATCHTASKS";
    public static String PREPAREQUORUMSTORE = "__PREPAREQUORUMSTORE";
    public static String COMMITQUORUMSTORE = "__COMMITQUORUMSTORE";

    protected volatile PBFTStateLog stateLog = new PBFTStateLog();

    public PBFTStateLog getStateLog() {
        return stateLog;
    }
    
    public TaskTableStore getTaskTableStore(){
        return ttstore;
    }

    public TaskTable getTaskTable(String ttname){

        TaskTable ttable = ttstore.get(ttname);

        if(ttable == null){
            ttable = new TaskTable();
            ttstore.put(ttname, ttable);
        }

        return ttable;
    }
    
    protected volatile IScheduler scheduler;

    public IScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(IScheduler scheduler) {
        this.scheduler = scheduler;
    }

    protected IClock clock;

    public IClock getClock() {
        return clock;
    }

    public void setClock(IClock clock) {
        this.clock = clock;
    }

    /**
     * Input buffer which is used to keep the received replies before they be able
     * to be checked and delivered to the application.
     */
    protected Buffer inbox = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());

    /**
     * Get the input buffer.
     * @return the input buffer.
     */
    protected Buffer getInbox() { return inbox;  }

    /**
     * Output buffer where the requests are kept until be able to be encrypted
     * and send to the server group.
     */
    protected Buffer outbox = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());

    /**
     * Get the output buffer.
     * @return the output buffer.
     */
    protected Buffer getOutbox() { return outbox;  }

    /**
     * Application buffer where the results are kept until be able to be
     * delivered to the application.
     */
    protected Buffer appbox = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());

    protected IMessageAuthenticator authenticator;


    public IMessageAuthenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(IMessageAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    protected IGroup group;
    
    public IGroup getLocalGroup(){
        return group;
    }

    public void setLocalGroup(IGroup g){
        group = g;
    }

    public int getServiceBFTResilience(){
        return (int)(Math.floor(getLocalGroup().getGroupSize()/3));
    }

    Architecture architecture = null;

    public Architecture getArchitecture() {
        return architecture;
    }

    public void setArchitecture(Architecture architecture) {
        this.architecture = architecture;        
    }

    public void buildup(){
        getArchitecture().buildup();
    }
    public void startup(){
        getArchitecture().startup();
    }

    protected boolean shutdown = false;
    
    public void shutdown(){
        getArchitecture().shutdown();
        shutdown = true;
    }


}
