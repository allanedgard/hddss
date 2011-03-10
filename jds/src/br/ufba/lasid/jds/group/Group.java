/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.group;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.util.ProcessList;

/**
 *
 * @author aliriosa
 */
public class Group<GroupID, ProcessID> implements IGroup<GroupID, ProcessID>{
    
    GroupID groupID;
    int size = 0;
    
    ProcessList<ProcessID> members = new ProcessList<ProcessID>();
    
    public void setID(GroupID id) {
        this.groupID = id;
    }

    public GroupID getID() {
        return this.groupID;
    }

    public int getGroupSize() {
        return this.members.size();
    }

    /**
     * This method must be revised.
     * @param size
     */
    public void setGroupSize(int size) {
        this.size = size;
    }

    public ProcessList<ProcessID> getMembers() {
        return members;
    }

    public synchronized void addMember(IProcess<ProcessID> process) {

        if(process!= null && !members.containsProcessByID(process.getID())){
            members.add(process);
        }
        
    }

    public boolean isMember(IProcess<ProcessID> process) {
        return members.containsProcessByID(process.getID());
    }

    public synchronized void removeMember(IProcess<ProcessID> process) {
        members.removeProcess(process);
    }

    public void makeGroupFromIDs(ProcessID[] processIDVector) {

        for(int i = 0; i < processIDVector.length; i++){
            IProcess<ProcessID> process = new IProcess<ProcessID>() {

                ProcessID processID;

                public ProcessID getID() {
                    return processID;
                }

                public void setID(ProcessID processID) {
                    this.processID = processID;
                }

            };

            process.setID(processIDVector[i]);
            
            addMember(process);
        }

    }

    @Override
    public String toString() {
        String m = "";
        String more = "";
        for(IProcess p : members){
            m += more + "s" + p.getID();
            more = ", ";
        }

        if(m.equals("")) m = "unavailable";

        
        return "Group{" + "gid=" + groupID + ", members=(" + m + ")}";
    }

    public synchronized IGroup<GroupID, ProcessID> minus(IProcess<ProcessID> p) {
        
        Group<GroupID, ProcessID> g = this;

        if(p!= null && p.getID()!=null){
            g = new Group<GroupID, ProcessID>();
            g.setID(this.getID());
            g.setGroupSize(this.getGroupSize());
            for(IProcess<ProcessID> member : this.getMembers()){
                if(!member.getID().equals(p.getID())){
                    g.addMember(member);
                }
            }
        }
        return g;
    }

    public synchronized IGroup<GroupID, ProcessID> plus(IProcess<ProcessID> p) {

        Group<GroupID, ProcessID> g = new Group<GroupID, ProcessID>();
        g.setID(this.getID());
        g.setGroupSize(this.getGroupSize());
        g.getMembers().addAll(this.getMembers());
        g.addMember(p);
        return g;
    }

    
}
