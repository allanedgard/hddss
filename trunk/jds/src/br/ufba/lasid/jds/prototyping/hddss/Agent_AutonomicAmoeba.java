package br.ufba.lasid.jds.prototyping.hddss;

import java.util.Hashtable;

public class Agent_AutonomicAmoeba extends Agent_AmoebaSequencer{

    double minTS  = 0;
    double maxTS  = 0;
    double ctrTS  = 0;

    double iarrv[];
    double larrv[];
    double meanIA = -1.0;

    long nrecv = 0;
    long nts = 0;
    
    double meanOVH = -1.0;
    
    double now = 0;
    double old = 0;
    
    double dmean = -1.0;
    double dmin  = -1.0;
    double dmax  = -1.0;

    double RCRef = 0.0;
    double RC    = 0.0;
    
    Hashtable<String, Integer> buffer = new Hashtable<String, Integer>();
    
    public Agent_AutonomicAmoeba() {
        super();
    }

    public void setDesiredResourceConsumption(String v){
        RCRef = Double.parseDouble(v);
    }

    public double computeSetPoint(){
        
        double maxOVH = (((double)(infra.nprocess - 1))/infra.nprocess);
        return (maxOVH * (RCRef - RC));

    }

    @Override
    public void setup() {
        super.setup();

        iarrv = new double[infra.nprocess];
        larrv = new double[infra.nprocess];

        for(int i = 0; i < infra.nprocess; i++) {
            iarrv[i]  = 0;
            larrv[i]  = 0;
        }
    }
    
    
    @Override
    public void receive(Message msg) {
        nrecv++;
        
        estimateDelay(msg);

        if(msg.type != APP){
                nts++;
        }

        super.receive(msg);

    }
    
    @Override
    public void setDeltaMax(String dt) {
            super.setDeltaMax(dt);
            dmax = DELTA;
    }

    public void estimateDelay(Message msg){
        double ro = infra.context.get(RuntimeSupport.Variable.ClockDeviation).<Double>value();
        int maxro = infra.context.get(RuntimeSupport.Variable.MaxClockDeviation).<Integer>value();
        if(msg.content instanceof Content_TimedCB){
            Content_TimedCB content = (Content_TimedCB) msg.content;
            Content_Acknowledge ack = content.vack[msg.destination];

            //compute the round-trip-time
            double rtt = msg.receptionTime - ack.lsendTime;
            
            //compute the remote proc time
            double ptime = (ack.rsendTime - ack.rrecvTime) * (1 - maxro * ro);

            double delay = (rtt - ptime)/2;
            
            if(dmean < 0) dmean = delay;

            dmean = 0.99 * dmean + 0.01 * delay;

            if(dmin < 0){
                dmin = dmean;                
                dmax = dmean;              
            }
            
            if(dmax < 0){
                dmax = dmean;                
            }
            
            if(delay > dmax) dmax = 1.1 * delay;
            if(delay < dmin) dmin = delay;

            dmin = dmin * 0.99999 + delay * 0.0001;
            dmax = dmax * 0.99999 + delay * 0.0001;
            computeRC();
        }
        
    }

    public void computeRC(){
        RC = 0;
        
        if(dmax > dmin){
            RC = (dmean - dmin)/(dmax - dmin);
        }
    }

    @Override
    public void setTS(String in)
    {
        super.setTS(in);
        maxTS = ts;
    }

    
    public int getMaxTS() {

        return (int) maxTS;
    }


    @Override
    public int getDelta() {
        // return super.getDeltaMax();
       
           if(dmax < 0)
            return super.getDelta();
        
        return (int) round(dmax, 0);
        /* */
    }

    
    /*
    @Override
    public int getDeltaMin() {
        if(dmin < 0)
            return super.getDeltaMin();
        
        return (int) round(dmin, 0);
    }
    */

    @Override
    public void deliver(Message msg) {
        super.deliver(msg);
        int clock = (int)infra.clock.value();

        if(msg.type == APP){

            iarrv[msg.sender] = clock - larrv[msg.sender];
            larrv[msg.sender] = clock;
            
            estimateTSMax();

        }
        
        old = now;
        now = clock;
        control();
    }

    public void estimateTSMax(){
        double sum = 0.0;
        for(int i =0; i < infra.nprocess; i++){
            sum += (double)iarrv[i];

            if(iarrv[i] > maxTS){
                maxTS = 1.1 * iarrv[i];
            }else{
                maxTS = 0.999 * maxTS + 0.001 * iarrv[i];
            }
        }

        sum = sum / infra.nprocess;

        if(meanIA < 0.0) meanIA = sum;

        meanIA = 0.9999 * meanIA + 0.0001 * sum;
        
        //System.nic_out.println("p" + ID + "meanIA="+meanIA);
        
    }

    private double round(double v, int dig){
        double pow = Math.pow(10, dig);
        return Math.ceil(v * pow)/pow;
    }

    public void control(){
        double error = computeSetPoint() - sensing();

        
        //double dtsOvh = (-1.0/maxTS) * ((infra.nprocess - 1)/((double)infra.nprocess)) * error;
        double dtsOvh = 2.0/3.0;
        
        double dt = now - old;
        
        double action =  1000 * dtsOvh * dt;
        
        ctrTS += action;

        if(ctrTS > maxTS) ctrTS = maxTS;
        
        if(ctrTS < 0) ctrTS = 0;
        
        actuate(ctrTS);
        
    }
    
    public double sensing(){
        double OVH = ((double)nts) / nrecv;

        if(meanOVH < 0) meanOVH = OVH;
        
        meanOVH = 0.9 * meanOVH + 0.1 * OVH;

        return meanOVH;
    }

    public void actuate(double value){
        ts = (int)value;
        //System.nic_out.println("p" + ID + "," + dmean + "," + meanOVH + "," +ts + "," + RC);
    }    
}
