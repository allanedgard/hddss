/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.ft;

import br.ufba.lasid.jds.cs.IServer;

/**
 *
 * @author aliriosa
 */
public interface IRecoverableServer<ProcessID> extends IServer<ProcessID>, IRecoverableProcess<ProcessID>{

}
