package br.ufba.lasid.jds.prototyping.hddss;

public class ProbabilisticEventGenerator extends EventGenerator {
    
    ProbabilisticEventGenerator() {
        super();
    }
    
    public void setDistribution(String dt) {
        r.setDistribution(dt);
    }

    @Override
    boolean hasEvent(int t) {
            return hasEvent();
    }
    
    @Override
    boolean hasEvent() {
            double x1;
            if (prob==0.0) {
                x1 = r.genericDistribution();
            } else x1 = prob;
            return (r.genericDistribution() <= x1);
    }
    
}
