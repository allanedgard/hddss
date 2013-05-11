package br.ufba.lasid.jds.prototyping.hddss;

public class FaultModelAgent_Crash extends FaultModelAgent {
    
    char tipo_inicial;
    boolean parado;
    Randomize r;
    double prob;
    
    FaultModelAgent_Crash(RuntimeContainer a) {
        super(a);
        r = new Randomize();
        prob = r.uniform();
        parado = false;
    }
    
    FaultModelAgent_Crash() {
        super();
        r = new Randomize();
        // prob = r.uniform();
        prob = .2;
        parado = false;
    }
    
    FaultModelAgent_Crash(RuntimeContainer a, double p) {
        super(a);
        r = new Randomize();
        prob = p;
        parado = false;
    }
    

    
    @Override
    public void increaseTick() {
            if (!parado) {
                infra.agent.setDone(true);
                
                if ( (r.uniform() <= prob)&& (infra.clock.value() >= 25) )  {
                    this.crash();
                    System.out.println("p" +infra.agent.getAgentID()+": falhou por crash em "+infra.clock.value());
                }
                else infra.execute();
                /*
                if ( (ag.ID == 1) && (ag.clock == 200) ) {
                    this.crash();
                    System.nic_out.println("p" +ag.ID+": falhou por crash em "+ag.clock);
                }
                else ag.processaRelogio(); 
                */
            }                
        }
    
    private void crash() {
        parado = true;
    }

    @Override
    public boolean status() {
        return !parado;
    }

}