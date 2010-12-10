/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.group;

import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.util.ProcessList;
/**
 *
 * @author aliriosa
 */
public interface Group<T> extends Process<T>{
    public void setGroupID(T id);
    public T getGroupID();

    public int getGroupSize();
    public void setGroupSize(int size);
    
    public ProcessList<T> getMembers();

    public void addMember(Process<T> process);

    public boolean isMember(Process<T> process);

    public void removeMember(Process<T> process);

    public void makeGroupFromIDs(T[] IDs);
    

}
