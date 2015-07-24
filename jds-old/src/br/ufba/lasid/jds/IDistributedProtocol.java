/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds;

/**
 *
 * @author aliriosa
 */
public interface IDistributedProtocol extends IProtocol{

    public IProcess getLocalProcess();

    public void setLocalProcess(IProcess process);

    public ISystemEntity getRemoteProcess();

    public void setRemoteProcess(ISystemEntity process);


}
