package br.ufba.lasid.jds.prototyping.hddss;

public abstract class Channel {
    
    Agent p_i, p_j;
    long lastDelivery;
    boolean contention;
    static final String TAG = "channel";
    Channel() {
    }
    public final void connect(Agent x, Agent y) {
        p_i = x;
        p_j = y;
        lastDelivery = 0;
        contention = false;
    }

    public void setContention(String dt) {
        contention = Boolean.parseBoolean(dt);
    }

    public final void deliverMsg(Message m, double a) {
      if ( this.status() ) {
          long d = 0; //delay();
          long at = (long) (d + a);
          System.out.println("-------------------- ");
          System.out.println("atraso na entrega: "+at);
          System.out.println("tempo de envio em p_i: "+m.physicalClock);
          System.out.println("tempo de envio global em p_i: "+m.actualSendingTime);
          System.out.println("clock de p_i: "+p_i.getInfra().clock.value());
          System.out.println("clock de p_j: "+p_j.getInfra().clock.value());
          System.out.println("clock global: "+p_j.getScenario().globalClock.value());
          System.out.println("rho de p_i: "+((Clock_Virtual) p_i.getInfra().clock).rho);
          System.out.println("rho de p_j: "+((Clock_Virtual) p_j.getInfra().clock).rho);
          
          // USA O TEMPO GLOBAL PARA RECEIVE
          //long nextDelivery =(p_j.getInfra().clock.value())+at;
          long nextDelivery =(p_j.getScenario().globalClock.value())+at;
          
          if (!contention) {
            /* Sem contenção */
            if (nextDelivery < lastDelivery) nextDelivery = lastDelivery+1;
          }
          else {
            /* Contenção */
            nextDelivery = max(lastDelivery,(p_j.getInfra().clock.value()))+at;
          }

//          p_j.infra.debug("channel process p" + p_j.ID + " delivers " + m.content + "at time " + nextDelivery);
//          System.out.println("(p" + p_i.ID + " => p" + p_j.ID + ") lastDelivery => " + lastDelivery + " ## at => " + at + " ## netdelay => " + a + " ## channeldelay => " + d +
//               " ## nextDelivery => " + nextDelivery + " ## msg => " + m.content);
          
          p_j.getInfra().nic_in.add(nextDelivery, m);
          lastDelivery=nextDelivery;
      }
    }
    
    long max (long a, long b) {
        return (a>b?a:b);
    }
    
    abstract int delay();
    
    abstract boolean status(); 
            
}
