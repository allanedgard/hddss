/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds;

import br.ufba.lasid.jds.util.IClock;
import br.ufba.lasid.jds.util.IScheduler;

/**
 *
 * @author aliriosa
 */
public interface IHost {


    public IClock getClock();

    public void setClock(IClock clock);

    public IScheduler getScheduler();

    public void setScheduler(IScheduler scheduler);


}
