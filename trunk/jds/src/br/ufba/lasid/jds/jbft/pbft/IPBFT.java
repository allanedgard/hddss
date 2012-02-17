/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.IDistributedProtocol;
import br.ufba.lasid.jds.architectures.Architecture;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.security.IMessageAuthenticator;
import br.ufba.lasid.jds.prototyping.hddss.IClock;
import br.ufba.lasid.jds.util.IScheduler;

/**
 *
 * @author aliriosa
 */
public interface IPBFT extends IDistributedProtocol{
    public Architecture getArchitecture();
    public void setArchitecture(Architecture architecture);

    public IMessageAuthenticator getAuthenticator();
    public void setAuthenticator(IMessageAuthenticator authenticator);

    public void setScheduler(IScheduler scheduler);
    public IScheduler getScheduler();

    public Object getLocalProcessID();

    public IGroup getLocalGroup();
    public void setLocalGroup(IGroup g);

    public IClock getClock();

    public void setClock(IClock clock);

    public  long getClockValue();

    public void shutdown();

}
