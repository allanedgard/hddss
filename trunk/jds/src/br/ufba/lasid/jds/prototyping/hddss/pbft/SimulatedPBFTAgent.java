/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageHandler;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.IPBFT;
import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.prototyping.hddss.Message;
import br.ufba.lasid.jds.util.JDSUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author aliriosa
 */
public class SimulatedPBFTAgent extends Agent implements IProcess<Integer>, Serializable, IPBFTAgent{

    protected transient IPBFT  protocol;
    protected transient IGroup group = new Group();
    static{
       JDSUtility.debug = true;
    }

    public IPBFT getProtocol() {
        return protocol;
    }

    public void setProtocol(IPBFT protocol) {
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
        //super.receive(msg);
//        synchronized(this){
            //PBFTCommunicator comm = (PBFTCommunicator)getProtocol().getCommunicator();
            //comm.receive((IMessage)msg.getContent());
  //      }

        ((MessageHandler)getProtocol().getArchitecture().getThead("__PBFTServant")).input((IMessage)msg.getContent());
        ((MessageHandler)getProtocol().getArchitecture().getThead("__PBFTServant")).handle();
    }

    @Override
    public void execute() {
//        synchronized(this){
            ((SimulatedScheduler)getProtocol().getScheduler()).execute();
  //      }
        //super.execute();

    }


    @Override
    public void shutdown() {
        getProtocol().shutdown();
        super.shutdown();

        shutdown= true;
    }

}
