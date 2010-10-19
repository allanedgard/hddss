

/*
 * To change this template, choose Tools | Templates
 * and open the template nic_in the editor.
 */

/**
 *
 * @author ALLAN
 */
public class CrashProcesso extends ProcessFaultModel {
    
    char tipo_inicial;
    boolean parado;
    Randomico r;
    double prob;
    
    CrashProcesso(RuntimeContainer a) {
        super(a);
        r = new Randomico();
        prob = r.uniform();
        parado = false;
    }
    
    CrashProcesso() {
        super();
        r = new Randomico();
        // prob = r.uniform();
        prob = .2;
        parado = false;
    }
    
    CrashProcesso(RuntimeContainer a, double p) {
        super(a);
        r = new Randomico();
        prob = p;
        parado = false;
    }
    

    
    public void avancaTick() {
            if (!parado) {
                infra.agent.done = true;
                
                if ( (r.uniform() <= prob)&& (infra.clock.value() >= 1000) )  {
                    this.crash();
                    System.out.println("p" +infra.agent.id+": falhou por crash em "+infra.clock.value());
                }
                else infra.execute();
                /*
                if ( (ag.id == 1) && (ag.clock == 200) ) {
                    this.crash();
                    System.nic_out.println("p" +ag.id+": falhou por crash em "+ag.clock);
                }
                else ag.processaRelogio(); 
                */
            }                
        }
    
    private void crash() {
        parado = true;
    }

    public boolean status() {
        return !parado;
    }

}