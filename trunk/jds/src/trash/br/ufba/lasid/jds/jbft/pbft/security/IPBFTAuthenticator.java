/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft.security;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTStateLog;
import br.ufba.lasid.jds.security.IAuthenticator;
import br.ufba.lasid.jds.util.ProcessList;

/**
 *
 * @author aliriosa
 */
public interface IPBFTAuthenticator extends IAuthenticator<PBFTMessage>{

    public PBFTMessage encrypt(PBFTMessage m, IProcess p);
    public PBFTMessage decrypt(PBFTMessage m, IProcess p);

    public void addProcess(IProcess p);
    public void delProcess(IProcess p);

    public ProcessList getProcessList();

    public void setMessageDigestAlgorithm(String algname);

    public String getMessageDigestAlgorithm();

    public void setMessageAuthenticationCodeAlgorithm(String algname);

    public String getMessageAuthenticationCodeAlgorithm();


    public String getDigest(PBFTStateLog state);

}
