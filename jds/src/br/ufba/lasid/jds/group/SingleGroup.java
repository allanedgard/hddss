/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.group;

import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.SingleProcess;
import br.ufba.lasid.jds.util.ProcessList;
import java.util.StringTokenizer;

/**
 *
 * @author aliriosa
 */
public class SingleGroup<T> implements Group<T>{
    T id;
    int size = 0;
    ProcessList<T> members = new ProcessList<T>();
    public void setGroupID(T id) {
        this.id = id;
    }

    public T getGroupID() {
        return this.id;
    }

    public T getID() {
        return getGroupID();
    }

    public void setID(T id) {
        setGroupID(id);
    }

    public int getGroupSize() {
        return this.members.size();
    }

    public void setGroupSize(int size) {
        //this.size = size;
    }

    public ProcessList<T> getMembers() {
        return members;
    }

    public void addMember(Process<T> process) {
        members.add(process);
    }

    public boolean isMember(Process<T> process) {
        return members.containsProcessByID(process.getID());
    }

    public void removeMember(Process<T> process) {
        members.removeProcess(process);
    }

    public void makeGroupFromIDs(T[] IDs) {

        for(int i = 0; i < IDs.length; i++){
            Process<T> process = new SingleProcess<T>();
            process.setID(IDs[i]);
            members.add(process);
        }

    }



}
