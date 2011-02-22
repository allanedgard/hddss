/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.util.IPayload;

/**
 *
 * @author aliriosa
 */
public interface IClient<T> extends IProcess<T> {

    public void receiveResult(IPayload content);

}
