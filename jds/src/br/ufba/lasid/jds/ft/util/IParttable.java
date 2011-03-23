/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.ft.util;

/**
 *
 * @author aliriosa
 */
public interface IParttable {

    public PartTree.PartEntry put(Long recid, PartTree.PartEntry entry) throws Exception;
    public PartTree.PartEntry get(Long recid) throws Exception;


}
