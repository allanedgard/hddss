/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.group;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.ISystemEntity;
import br.ufba.lasid.jds.util.ProcessList;
import java.io.Serializable;
/**
 *
 * @author aliriosa
 */
public interface IGroup<GroupID, ProcessID> extends Serializable, ISystemEntity{
    
    public void setID(GroupID groupID);
    public GroupID getID();

    public int getGroupSize();
    
    public void setGroupSize(int size);
    
    public ProcessList<ProcessID> getMembers();

    public void addMember(IProcess<ProcessID> process);

    public boolean isMember(IProcess<ProcessID> process);

    public void removeMember(IProcess<ProcessID> process);

    public void makeGroupFromIDs(ProcessID[] IDs);

    public IGroup<GroupID, ProcessID> minus(IProcess<ProcessID> p);
    public IGroup<GroupID, ProcessID> plus(IProcess<ProcessID> p);

//    public ProcessID next(ProcessID pid);

    public IProcess<ProcessID> getMember(int i);
    

}
