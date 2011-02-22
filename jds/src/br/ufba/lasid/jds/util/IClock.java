/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

/**
 *
 * @author aliriosa
 */
public interface IClock {
    public long value();
    public long tickValue();
    public void adjustValue(long v);
    public void adjustTickValue(long v);
    public void adjustCorrection(long c);

}
