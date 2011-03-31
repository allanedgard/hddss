/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.comm.IMessage;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTMessage implements IMessage{
    @Override
    public abstract String toString();
    
}