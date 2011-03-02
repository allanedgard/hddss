/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.comm.communicators.PBFTCommunicator;
import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.prototyping.hddss.Message;
import java.util.ArrayList;
import java.util.StringTokenizer;
import br.ufba.lasid.jds.jbft.pbft.PBFT;

/**
 *
 * @author aliriosa
 */
public class SimulatedPBFTAgent extends Agent implements IProcess<Integer>, IPBFTAgent{

    protected PBFT  protocol;
    protected IGroup group = new Group();


    public PBFT getProtocol() {
        return protocol;
    }

    public void setProtocol(PBFT protocol) {
        this.protocol = protocol;
    }
    
    public Integer getID() {
        return new Integer(this.ID);
    }

    public void setID(Integer id) {
        //do nothing ... agent.ID isn't going to be modified.
        //this.ID = ID.intValue();
    }

    public IGroup getGroup(){
        return this.group;
    }
    
    public void setGroupSize(String size) {
        group.setGroupSize(new Integer(size));
    }

    public void setServerGroupAddress(String id) {
        group.setID(new Integer(id));
    }


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

        group.makeGroupFromIDs(IDs);
    }

    @Override
    public void receive(Message msg) {
        super.receive(msg);

        PBFTCommunicator comm = (PBFTCommunicator)getProtocol().getCommunicator();
        comm.receive((IMessage)msg.getContent());
    }

    @Override
    public void shutdown() {
        getProtocol().shutdown();
        super.shutdown();

        shutdown= true;
    }

    @Override
    public void execute() {
        ((SimulatedSchedulerHook)getProtocol().getScheduler()).execute();
        super.execute();
    }




}
