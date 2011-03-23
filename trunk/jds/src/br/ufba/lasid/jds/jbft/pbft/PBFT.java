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
import br.ufba.lasid.jds.jbft.pbft.util.PBFTStateLog;
import br.ufba.lasid.jds.util.ITask;

/**
 *
 * @author aliriosa
 */
public class PBFT extends DistributedProtocol{

    /**
     * State a table with tasks related to PBFT, in special these ones which are
     * associated to timers (such as, active-status period, primary response 
     * timeout, batching request timeout etc.).
     */
    protected  TaskTableStore ttstore = new TaskTableStore();

    public static String REQUESTTASKS       = "REQUESTTASKS";
    public static String VIEWCHANGETASKS    = "VIEWCHANGETASKS";
    public static String BATCHTASKS         = "BATCHTASKS";
    public static String PREPAREQUORUMSTORE = "__PREPAREQUORUMSTORE";
    public static String COMMITQUORUMSTORE = "__COMMITQUORUMSTORE";
    public static String CHECKPOINTQUORUMSTORE = "__CHECKPOINTQUORUMSTORE";
    public static String METADATAQUORUMSTORE = "__METADATAQUORUMSTORE";
    public static String BAGQUORUMSTORE = "__BAGQUORUMSTORE";


    protected  PBFTStateLog stateLog = new PBFTStateLog();
    

    public PBFTStateLog getStateLog() {
        return stateLog;
    }
    
    public TaskTableStore getTaskTableStore(){
        return ttstore;
    }

    public TaskTable getTaskTable(String ttname){
        TaskTable ttable = ttstore.get(ttname);
        synchronized(this){
            if(ttable == null){
                ttable = new TaskTable();
                ttstore.put(ttname, ttable);
            }
        }
        return ttable;
        
    }
    
    protected  IScheduler scheduler;

    public IScheduler getScheduler() {
        return scheduler;
    }

    public Object getLocalProcessID(){
        return getLocalProcess().getID();
    }
    public void setScheduler(IScheduler scheduler) {
        this.scheduler = scheduler;
    }

    protected  IClock clock;

    public IClock getClock() {
        return clock;
    }

    public void setClock(IClock clock) {
        this.clock = clock;
    }

    public  long getClockValue(){
        return getClock().value();
    }

    protected  IMessageAuthenticator authenticator;


    public IMessageAuthenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(IMessageAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    protected  IGroup group;
    
    public IGroup getLocalGroup(){
        return group;
    }

    public void setLocalGroup(IGroup g){
        group = g;
    }

    public int getServiceBFTResilience(){
        return (int)(Math.floor(getLocalGroup().getGroupSize()/3));
    }

    protected  Architecture architecture = null;

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
