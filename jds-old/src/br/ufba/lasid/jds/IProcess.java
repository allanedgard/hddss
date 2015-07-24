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
    
    public static final long serialVersionUID = -7348058540247979182L;

    public ProcessID getID();

    public void setID(ProcessID processID);
    
}
