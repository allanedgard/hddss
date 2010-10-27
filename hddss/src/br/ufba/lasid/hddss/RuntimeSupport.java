/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss;

/**
 *
 * @author aliriosa
 */
public interface RuntimeSupport {
    public enum Variable{
        NumberOfAgents, MaxDeviation, Mode, Type, Debug, MaxSimulationTime,
        FinalTime, RxDelayTrace, DlvDelayTrace, TxDelayTrace, QueueDelayTrace,
        Network, StdOutput, StdInput, ClockDeviation, MaxClockDeviation
    }
    public Value get(Variable variable);
    public <U> void set(Variable variable, U value);
    public Value get(String name);
    public <U> void set(String name, U value);
    public void perform(RuntimeContainer rs);
    public void ok();

}
