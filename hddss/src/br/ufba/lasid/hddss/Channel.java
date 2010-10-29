package br.ufba.lasid.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template nic_in the editor.
 */
/**
 *
 * @author allan
 */
public abstract class Channel {
    
    Agent p_i, p_j;
    int lastDelivery;
    boolean contention;
    static final String TAG = "channel";
    Channel() {
    }
    public final void connect(Agent x, Agent y) {
        p_i = x ;
        p_j = y;
        lastDelivery = 0;
        contention = false;
    }

    public void setContention(String dt) {
        contention = Boolean.parseBoolean(dt);
    }

    public final void deliverMsg(Message m, double a) {
        if ( this.status() ) {
            int at = (int) (delay()+a);
            int nextDelivery =(int)p_j.infra.clock.value()+at;
            
            if (!contention) {

                     /*  Sem contenção
                     */
                    if (nextDelivery < lastDelivery)
                        nextDelivery = lastDelivery+1;
            }
            else {
             /*  Contenção
             */ 
                    nextDelivery = max(lastDelivery,(int)p_j.infra.clock.value())+at;
            };

            p_j.infra.debug("p"+p_j.id+" entrega = "+nextDelivery);
            p_j.infra.nic_in.add(nextDelivery, m);
            lastDelivery=nextDelivery;
        }
    }
    
    int max (int a, int b) {
        return a>b?a:b;
    }
    
    abstract int delay();
    
    abstract boolean status(); 
            
}
