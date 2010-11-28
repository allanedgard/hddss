/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.group;

import br.ufba.lasid.jds.Process;
/**
 *
 * @author aliriosa
 */
public interface Group<T> extends Process<T>{
    public void setGroupID(T id);
    public T getGroupID();

}
