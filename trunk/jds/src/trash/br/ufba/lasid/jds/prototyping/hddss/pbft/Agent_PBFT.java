/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.IRemoteProcess;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.group.Group;
import trash.br.ufba.lasid.jds.jbft.pbft.PBFT2;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.prototyping.hddss.pbft.SimulatedScheduler;
import trash.br.ufba.lasid.jds.prototyping.hddss.cs.Agent_ServiceComponent;
import br.ufba.lasid.jds.prototyping.hddss.pbft.comm.SimulatedPBFTCommunicator;
import trash.br.ufba.lasid.jds.prototyping.hddss.pbft.security.PBFTSimulatedAuthenticator;
import trash.br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.ProcessList;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author aliriosa
 */
public class Agent_PBFT extends Agent_ServiceComponent implements IProcess<Integer>{

    IGroup group = new Group();

    public void setGroupList(String sList){
        sList = sList.replaceAll("\\[", "");
        sList = sList.replaceAll("\\]", "");
        sList = sList.replaceAll(" ", "");
        StringTokenizer tokenizer = new StringTokenizer(sList, ",", false);
        ArrayList<Integer> aList = new ArrayList<Integer>();
        while(tokenizer.hasMoreElements()){
            aList.add(new Integer(tokenizer.nextToken()));
        }

        
        Integer[] IDs = new Integer[aList.size()];
        System.arraycopy(aList.toArray(), 0, IDs, 0, IDs.length);
        makeGroupFromIDs(IDs);
    }
    
    public void setCheckpointPeriod(String period){
        ((PBFT2)getProtocol()).setCheckPointPeriod(new Long(period));
    }
    public void setBatchingSize(String size){
        getProtocol().getContext().put(PBFT2.BATCHINGSIZE, new Integer(size));
    }

    public void setRejuvenationWindow(String size){
        getProtocol().getContext().put(PBFT2.REJUVENATIONWINDOW, new Integer(size));
    }

    public int getRejuvenationWindow(){
        return (Integer) getProtocol().getContext().get(PBFT2.REJUVENATIONWINDOW);
    }

    public void setBatchingTimeout(String timeout){
        getProtocol().getContext().put(PBFT2.BATCHINGTIMEOUT, new Long(timeout));
    }

    public void setViewChangeRetransmittionTimeout(String timeout){
        getProtocol().getContext().put(PBFT2.VIEWCHANGERETRANSMITIONTIMEOUT, new Long(timeout));
    }
    
    public void setGroupSize(String size){
        setGroupSize(Integer.parseInt(size));
    }

    public void setServerGroupAddress(String addr){
        this.setGroupID(new Integer(addr));
        getProtocol().setRemoteProcess((IRemoteProcess)this.getGroup());
    }

    public void setCurrentPrimary(String addr){
        getProtocol().getContext().put(PBFT2.GROUPLEADER, new Integer(addr));
    }

    public void setCurrentPrimary(Integer addr){
        getProtocol().getContext().put(PBFT2.GROUPLEADER, addr);
    }

    public void setPrimaryFaultTimeout(String t){
        ((PBFT2)getProtocol()).setPrimaryFaultTimeout(new Long(t));
    }

    public void setCurrentView(String v){
        ((PBFT2)getProtocol()).setCurrentView(new Integer(v));
    }

    public IGroup getGroup() {
        return group;
    }

    public void setGroup(IGroup group) {
        this.group = group;
    }


    @Override
    public void setup() {
        super.setup();
        setProtocol(new PBFT2());
        getProtocol().getContext().put(PBFT2.LOCALGROUP, getGroup());
        getProtocol().getContext().put(PBFT2.CLOCKSYSTEM, infra.clock);
        getProtocol().getContext().put(PBFT2.DEBUGGER, infra);
        getProtocol().getContext().put(PBFT2.REQUESTBUFFER, new Buffer());
        getProtocol().getContext().put(PBFT2.PREPREPAREBUFFER, new Buffer());
        getProtocol().getContext().put(PBFT2.PREPAREBUFFER, new Buffer());
        getProtocol().getContext().put(PBFT2.COMMITBUFFER, new Buffer());
        getProtocol().getContext().put(PBFT2.COMMITTEDBUFFER, new Buffer());
        getProtocol().getContext().put(PBFT2.REPLYBUFFER, new Buffer());
        getProtocol().getContext().put(PBFT2.CHECKPOINTBUFFER, new Buffer());
        getProtocol().getContext().put(PBFT2.CHANGEVIEWBUFFER, new Buffer());
        getProtocol().getContext().put(PBFT2.CHANGEVIEWACKBUFFER, new Buffer());
        getProtocol().getContext().put(PBFT2.REJUVENATIONWINDOW, new Buffer());
        getProtocol().getContext().put(PBFT2.CHANGEVIEWCERTIFICATEBUFFER, new Buffer());

        getProtocol().getContext().put(
            PBFT2.CLIENT2SERVERAUTHENTICATOR,
            new PBFTSimulatedAuthenticator(PBFT2.CLIENT2SERVERAUTHENTICATOR)
        );

        getProtocol().getContext().put(
            PBFT2.SERVER2SERVERAUTHENTICATOR,
            new PBFTSimulatedAuthenticator(PBFT2.SERVER2SERVERAUTHENTICATOR)
        );

//        getProtocol().getContext().put(
//            PBFT2.SCHEDULER,
//            new SimulatedScheduler()
//        );
/*
        getProtocol().getContext().put(
            PBFT2.PRIMARYFDSCHEDULER,
            new PBFTPrimaryFDScheduler(
                (PBFT2)getProtocol(),
                (IScheduler)infra.context.get(RuntimeSupport.Variable.Scheduler).value()
            )
        );
 * 
 */

        /*

        getProtocol().getContext().put(
            PBFT2.BATCHSCHEDULER,
            new PBFTBatchingTimeoutScheduler(
                (PBFT2)getProtocol(),
                (IScheduler)infra.context.get(RuntimeSupport.Variable.Scheduler).value()
            )
        );

         * 
         */
        /*
        getProtocol().getContext().put(
            PBFT2.CHANGEVIEWRETRANSMITIONSCHEDULER,
            new PBFTViewChangeRetransmittionScheduler(
                (PBFT2)getProtocol(),
                (IScheduler)infra.context.get(RuntimeSupport.Variable.Scheduler).value()
            )
        );
         * 
         */

        getProtocol().setCommunicator(new SimulatedPBFTCommunicator(this));
        getProtocol().setLocalProcess(this);
        
    }

    @Override
    public void multicast(IMessage m, IGroup group) {
        getProtocol().getCommunicator().multicast(m, group);
    }

    public void setGroupID(Integer id) {
        group.setID(id);
    }

    public Integer getGroupID() {
        return (Integer)group.getID();
    }

    @Override
    public void receive(br.ufba.lasid.jds.prototyping.hddss.Message msg) {
        super.receive(msg);
        PBFTMessage m = (PBFTMessage)(msg.getContent());
        /**
         * [FIX] We must guarantee that only RECEIVEREQUEST, RECEIVEREPLY,
         * RECEIVEPREPREPARE, RECEIVEPREPEPARE, RECEIVECOMMIT, NEWVIEW,
         * CHANGEVIEW, CHECKPOINT and FETCHSTATE can be executed.
         * In addition, some of these messages must be accepted by members of
         * the group.
         * 
         */
//        getProtocol().doAction(m);
    }

    public int getGroupSize() {
        return group.getMembers().size();
    }

    public void setGroupSize(int size) {
        //do nothing
        //group.setGroupSize(size);
    }

    public ProcessList<Integer> getMembers() {
        return group.getMembers();
    }

    public void addMember(IProcess<Integer> process) {
        group.addMember(process);
    }

    public boolean isMember(IProcess<Integer> process) {
        return group.isMember(process);
    }

    public void removeMember(IProcess<Integer> process) {
        group.removeMember(process);
    }

    public void makeGroupFromIDs(Integer[] IDs) {
        group.makeGroupFromIDs(IDs);
    }



}
