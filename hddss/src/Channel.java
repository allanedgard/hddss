/*
 * To change this template, choose Tools | Templates
 * and open the template nic_in the editor.
 */
import java.lang.reflect.Method;
/**
 *
 * @author allan
 */
public abstract class Channel {
    
    Agent p_i, p_j;
    int ultimaEntrega;
    boolean contention;
    static final String TAG = "channel";
    Channel() {
    }
    public final void connect(Agent x, Agent y) {
        p_i = x ;
        p_j = y;
        ultimaEntrega = 0;
        contention = false;
    }

    public void setContention(String dt) {
        contention = Boolean.parseBoolean(dt);
    }

    public final void entregaMensagem(Message m, double a) {
        if ( this.status() ) {
            int at = (int) (atraso()+a);
            int proxEntrega =(int)p_j.infra.clock.value()+at;
            
            if (!contention) {

                     /*  Sem contenção
                     */
                    if (proxEntrega < ultimaEntrega)
                        proxEntrega = ultimaEntrega+1;
            }
            else {
             /*  Contenção
             */ 
                    proxEntrega = maximo(ultimaEntrega,(int)p_j.infra.clock.value())+at;
            };

            p_j.infra.debug("p"+p_j.id+" entrega = "+proxEntrega);
            p_j.infra.nic_in.adiciona(proxEntrega, m);
            ultimaEntrega=proxEntrega;
        }
    }
    
    int maximo (int a, int b) {
        return a>b?a:b;
    }
    
    abstract int atraso();
    
    abstract boolean status(); 
            
}
