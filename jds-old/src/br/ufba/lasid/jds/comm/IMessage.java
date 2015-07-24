/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

import br.ufba.lasid.jds.prototyping.hddss.IProcessable;
import java.io.Serializable;

/**
 *
 * @author aliriosa
 */
public interface IMessage extends Serializable, IProcessable{
    public static final long serialVersionUID = 8186032393583278399L;

}
