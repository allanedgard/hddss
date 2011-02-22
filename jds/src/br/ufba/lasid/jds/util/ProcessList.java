/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

import br.ufba.lasid.jds.IProcess;
import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class ProcessList<T> extends ArrayList<br.ufba.lasid.jds.IProcess<T>>{

    public IProcess<T> getProcessByID(T ID){
        for(IProcess<T> process : this){
            if(process.getID().equals(ID))
                return process;
        }

        return null;
    }

    public void removeProcessByID(T ID){

        IProcess<T> process = getProcessByID(ID);
        
        if(process != null)
            this.remove(process);
    }

    public void removeProcess(IProcess<T> process){
        process = getProcessByID(process.getID());
        if(process != null)
            this.remove(process);
    }

    public boolean containsProcessByID(T ID){
        IProcess<T> process = getProcessByID(ID);
        return (process != null);
    }
}
