/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.fmm;

/**
 *
 * @author aliriosa
 */
public abstract class Mode {

    protected MultiModeMachine machine = null;

    public Mode(int imode, MultiModeMachine machine){
        this.machine = machine;
        this.machine.register(imode, this);
    }
    
    public boolean able(){
        return false;
    }

    public abstract void enter();

    public abstract void exit();

    @Override
    public String toString() {
        return "NULL";
    }

    public MultiModeMachine getMachine(){
       return this.machine;
    }

    public void swap(){
       getMachine().swap();
    }

    
}
