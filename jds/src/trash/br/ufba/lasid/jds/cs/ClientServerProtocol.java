/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.cs;

import br.ufba.lasid.jds.DistributedProtocol;
import trash.br.ufba.lasid.jds.factories.ClientServerActionFactory;
import trash.br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class ClientServerProtocol extends DistributedProtocol{

    @Override
    public void doAction(Wrapper w) {
        perform(ClientServerActionFactory.create(w));
    }

}
