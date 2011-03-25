/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.fmm;

import br.ufba.lasid.jds.util.JDSUtility;

/**
 *
 * @author aliriosa
 */
public class MultiModeMachine {

    protected Modetable modetable = new Modetable();
    protected int currentMODE = -1;

    public void register(int imode, Mode mode){
        modetable.put(imode, mode);
    }

    public void unregister(int imode){
        modetable.remove(imode);
    }

    public void switchTo(int imode){
       Mode from = null;
       Mode to   = null;
        Mode mode = modetable.get(currentMODE);
        if(mode != null){
            mode.exit();
            from = mode;
        }
        
        mode = modetable.get(imode);

        if(mode != null){
            currentMODE = imode;
            mode.enter();
            to = mode;
        }

        JDSUtility.debug(
          "[MultiModeMachine:switchTo(mode)] it has switched " +
          "from " + (from  == null ? "NULL" : from) + " " +
          "state to " + (to == null ? "NULL" : to) + " state."
        );
    }

}
