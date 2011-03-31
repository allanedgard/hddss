/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

import br.ufba.lasid.jds.IData;
/**
 *
 * @author aliriosa
 */
public interface IPayload<K, V> extends Wrapper<K, V>, IData {

   public int getSizeInBytes();
}
