/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.fmm;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.ISystemEntity;
import br.ufba.lasid.jds.adapters.IEventListener;
import br.ufba.lasid.jds.architectures.Architecture;
import br.ufba.lasid.jds.comm.MessageQueue;
import br.ufba.lasid.jds.comm.communicators.ICommunicator;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.fmm.MultiModeMachine;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewACK;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetch;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMetaData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTProcessingToken;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.security.IMessageAuthenticator;
import br.ufba.lasid.jds.util.IClock;
import br.ufba.lasid.jds.util.IScheduler;
import java.lang.reflect.Method;

/**
 *
 * @author aliriosa
 */
public class PBFTServerMultiModeMachine extends MultiModeMachine implements IPBFTServer{
    IPBFTServer pbft;
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public PBFTServerMultiModeMachine(IPBFTServer pbft) {
        this.pbft = pbft;
        new PBFTRunning(this);
        new PBFTOverloaded(this);
        new PBFTChanging(this);        
        switchTo(PBFTModes.RUNNING);
    }

    public IPBFTServer getProtocol(){
        return pbft;
    }

    public PBFTServerMode getCurrentMode(){
        return (PBFTServerMode)modetable.get(this.currentMODE);
    }
    
    public void handle(PBFTRequest rq) {
        getCurrentMode().handle(rq);
    }

    public void handle(PBFTPrePrepare ppr) {
        getCurrentMode().handle(ppr);
    }

    public void handle(PBFTPrepare pr) {
        getCurrentMode().handle(pr);
    }

    public void handle(PBFTCommit cm) {
        getCurrentMode().handle(cm);
    }

    public void handle(PBFTStatusActive sta) {
        getCurrentMode().handle(sta);
    }

    public void handle(PBFTFetch ft) {
        getCurrentMode().handle(ft);
    }

    public void handle(PBFTMetaData mdt) {
        getCurrentMode().handle(mdt);
    }

    public void handle(PBFTData dt) {
        getCurrentMode().handle(dt);
    }

    public void handle(PBFTCheckpoint ck) {
        getCurrentMode().handle(ck);
    }

    public void handle(PBFTBag bg) {
        getCurrentMode().handle(bg);
    }

    public void handle(PBFTProcessingToken tk) {
        getCurrentMode().handle(tk);
    }


    public void handle(PBFTChangeView cv) {
        getCurrentMode().handle(cv);
    }

    public void handle(PBFTChangeViewACK cva) {
        getCurrentMode().handle(cva);
    }

    public void handle(PBFTNewView nwv) {
        getCurrentMode().handle(nwv);
    }

    public MessageQueue getQueue(String name) {
        return getCurrentMode().getQueue(name);
    }

    public long getCurrentPrePrepareSEQ() {
        return getProtocol().getCurrentPrePrepareSEQ();
    }

    public long getCurrentExecuteSEQ() {
        return getProtocol().getCurrentExecuteSEQ();
    }

    public long getCurrentPrepareSEQ() {
        return getProtocol().getCurrentPrepareSEQ();
    }

    public long getCurrentCommitSEQ() {
        return getProtocol().getCurrentCommitSEQ();
    }

    public void emitChangeView() {
        getProtocol().emitChangeView();
    }

    public void installNewView() {
        getProtocol().installNewView();
    }

    public Object getLocalServerID() {
        return getProtocol().getLocalServerID();
    }

    public IMessageAuthenticator getAuthenticator() {
        return getProtocol().getAuthenticator();
    }

    public long getSlidingWindowSize() {
        return getProtocol().getSlidingWindowSize();
    }

    public Architecture getArchitecture() {
        return getProtocol().getArchitecture();
    }

    public void setArchitecture(Architecture architecture) {
        getProtocol().setArchitecture(architecture);
    }

    public void setAuthenticator(IMessageAuthenticator authenticator) {
        getProtocol().setAuthenticator(authenticator);
    }

    public void setScheduler(IScheduler scheduler) {
        getProtocol().setScheduler(scheduler);
    }

    public IScheduler getScheduler() {
        return getProtocol().getScheduler();
    }

    public Object getLocalProcessID() {
        return getProtocol().getLocalProcessID();
    }

    public IGroup getLocalGroup() {
        return getProtocol().getLocalGroup();
    }

    public void setLocalGroup(IGroup g) {
        getProtocol().setLocalGroup(g);
    }

    public IClock getClock() {
        return getProtocol().getClock();
    }

    public void setClock(IClock clock) {
        getProtocol().setClock(clock);
    }

    public long getClockValue() {
        return getProtocol().getClockValue();
    }

    public void shutdown() {
        getProtocol().shutdown();
    }

    public IProcess getLocalProcess() {
        return getProtocol().getLocalProcess();
    }

    public void setLocalProcess(IProcess process) {
        getProtocol().setLocalProcess(process);
    }

    public ISystemEntity getRemoteProcess() {
        return getProtocol().getRemoteProcess();
    }

    public void setRemoteProcess(ISystemEntity process) {
        getProtocol().setRemoteProcess(process);
    }

    public void setCommunicator(ICommunicator comm) {
        getProtocol().setCommunicator(comm);
    }

    public ICommunicator getCommunicator() {
        return getProtocol().getCommunicator();
    }

    public void setCheckpointPeriod(long period) {
        getProtocol().setCheckpointPeriod(period);
    }

    public void setCheckpointFactor(Long factor) {
        getProtocol().setCheckpointFactor(factor);
    }

    public void setBatchSize(int bsize) {
        getProtocol().setBatchSize(bsize);
    }

    public void setRejuvenationWindow(long rwindow) {
        getProtocol().setRejuvenationWindow(rwindow);
    }

    public void setBatchTimeout(Long btimeout) {
        getProtocol().setBatchTimeout(btimeout);
    }

    public void setChangeViewRetransmissionTimeout(long cvtimeout) {
        getProtocol().setChangeViewRetransmissionTimeout(cvtimeout);
    }

    public void setCurrentPrimaryID(Object pid) {
        getProtocol().setCurrentPrimaryID(pid);
    }

    public void setPrimaryFaultTimeout(Long pftimeout) {
        getProtocol().setPrimaryFaultTimeout(pftimeout);
    }

    public void setCurrentViewNumber(Integer viewn) {
        getProtocol().setCurrentViewNumber(viewn);
    }

    public void setSendStatusPeriod(long ssperiod) {
        getProtocol().setSendStatusPeriod(ssperiod);
    }

    public void setSlidingWindowSize(Long swsize) {
        getProtocol().setSlidingWindowSize(swsize);
    }

    public void loadState() {
        getProtocol().loadState();
    }

    public void schedulePeriodicStatusSend() {
        getProtocol().schedulePeriodicStatusSend();
    }

    public void emitFetch() {
        getProtocol().emitFetch();
    }

    public void setServer(IServer server) {
        getProtocol().setServer(server);
    }

    public IServer getServer() {
        return getProtocol().getServer();
    }

    public void addListener(IEventListener listener, Method m) {
        getProtocol().addListener(listener, m);
    }
}
