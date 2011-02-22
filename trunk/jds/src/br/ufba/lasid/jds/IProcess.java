/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

import java.io.Serializable;

/**
 *
 * @author aliriosa
 */
public interface IProcess<ProcessID> extends Serializable, ISystemEntity{

    public ProcessID getID();

    public void setID(ProcessID processID);
    
}
