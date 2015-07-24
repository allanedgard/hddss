/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

/**
 *
 * @author aliriosa
 */
public class BaseProcess<ProcessID> implements IProcess<ProcessID>{

    ProcessID id;

    public BaseProcess() {
    }

    public BaseProcess(ProcessID id) {
        this.id = id;
    }


    public ProcessID getID() {
        return id;
    }

    public void setID(ProcessID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        IProcess<ProcessID> process = (IProcess<ProcessID>) obj;
        return (this.id != null && this.id.equals(process.getID()));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public static IProcess create(Object id){
        return new BaseProcess(id);
    }

    @Override
    public String toString() {
        return "Process{" + "id=" + id + '}';
    }

    

}
