/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.acceptors;

/**
 *
 * @author aliriosa
 */
public interface IAcceptor<IN> {

    public boolean accept(IN in);

}
