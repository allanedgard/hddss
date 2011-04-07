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
    protected TransitionList transitions = new TransitionList();

    public void register(int imode, Mode mode){
        modetable.put(imode, mode);
    }

    public void unregister(int imode){
        modetable.remove(imode);
    }

    public void addTransition(int from, int to){
       if(!transitions.contains(from, to)){
          transitions.add(new Transition(this, from, to));
          Mode mto = getMode(to);
          Mode mfrom = getMode(from);
          JDSUtility.debug("[MultiModeMachine:swap(mode)] it has add a new transision (" + (mfrom  == null ? "NULL" : mfrom) + " ==> " + (mto == null ? "NULL" : mto) + ")");
       }
    }

    public void swap(){
       for(Transition t : transitions){
          if(t.able()){
             t.swap();
             break;
          }
       }
    }

    public void swap(int imode){
        Mode mode = modetable.get(currentMODE);
        if(mode != null){
            mode.exit();
        }

        currentMODE = imode;
        
        mode = modetable.get(currentMODE);

        if(mode != null){
            mode.enter();
        }
        
    }

    public Mode getMode(int i){
       return modetable.get(i);
    }

}
