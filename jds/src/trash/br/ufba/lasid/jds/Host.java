package trash.br.ufba.lasid.jds;

import br.ufba.lasid.jds.util.IClock;
import br.ufba.lasid.jds.util.IScheduler;

/**
 * @author aliriosa
 */
public class Host implements IHost{

    protected IClock clock;
    protected IScheduler scheduler;

    public Host(){
        
    }

    public Host(IClock clock, IScheduler scheduler) {
        this.clock = clock;
        this.scheduler = scheduler;
    }    
    
    public IClock getClock() {  
        return clock;
    }

    public void setClock(IClock clock) {  
        this.clock = clock;
    }

    public IScheduler getScheduler() {   
        return scheduler;
    }

    public void setScheduler(IScheduler scheduler){
        this.scheduler = scheduler;
    }
}