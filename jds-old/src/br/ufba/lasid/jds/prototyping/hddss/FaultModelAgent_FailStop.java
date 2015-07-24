package br.ufba.lasid.jds.prototyping.hddss;

public class FaultModelAgent_FailStop extends FaultModelAgent {

    boolean parado;
    Randomize r;
    double prob;
    
    FaultModelAgent_FailStop(RuntimeContainer a, double p) {
        super(a);
        r = new Randomize();
        prob = p;
        parado = false;
    }
    
    FaultModelAgent_FailStop(RuntimeContainer a) {
        super(a);
        r = new Randomize();
        prob = r.uniform();
        parado = false;
    }
    @Override
    public void increaseTick() {
            if (!parado)
                infra.agent.setDone(true);
                if (r.uniform() <= prob) {
                    parado = true;
                }
                else infra.execute();
        }
    
    @Override
    public boolean status() {
        return !parado;
    }
    
}
