import java.util.Hashtable;

/*
 * To change this template, choose Tools | Templates
 * and open the template nic_in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class AgenteAutonomicTimedCB5 extends AgenteTimedCB implements Controller{
    double MTBS = ts;
    double TSMaximun = 0.000;
    double TSMinimum = 0.000;
    double TimeSilence = ts;

    long numberOfCtlMsg = 0;
    long numberOfMsg = 0;

    double refBlocking = 0.0;
    double refOverhead = 0.0;

    double blockingRelevance = 1.0;
    double overheadRelevance = 1.0;

    double ctrlBlocking = 0.0;
    double ctrlOverhead = 0.0;

    int numberOfMsgDeliveried = 0;
    double totBlockingTime = 0;
    double meanBlocking = -1.0;
    double meanOverhead = -1.0;
            
    /*
     *  Modificação para estimar o ts         
     */
    int [] timeLastArrival;
    int [] meanTimeBetweenArrival;

   double wOvhd;
   double wBlck;


    @SuppressWarnings("UseOfObsoleteCollectionType")
    Hashtable<String, Integer> buffer = new Hashtable<String, Integer>();

    public void setMaximumTS(String v){
        TSMaximun = Double.parseDouble(v);
    }

    public void setMinimumTS(String v){
        TSMinimum = Double.parseDouble(v);
    }

    public void setSPBlockingTime(String v){
        refBlocking = Double.parseDouble(v);
    }

    public void setSPOverhead(String v){
        refOverhead = Double.parseDouble(v);
    }

    public  void setWBlockingTime(String v){
        blockingRelevance = Double.parseDouble(v);
        this.adjust();
    }

    public void setWOverhead(String v){
        overheadRelevance = Double.parseDouble(v);
        this.adjust();
    }

    public void adjust() {
        wOvhd = overheadRelevance / (overheadRelevance + blockingRelevance);
        wBlck = blockingRelevance / (overheadRelevance + blockingRelevance);
    }

    public AgenteAutonomicTimedCB5() {
        super();
    }

    @Override
    public void setup() {
        /*
        *  Modificação para estimar o ts
        */
        super.setup();
        meanTimeBetweenArrival = new int[infra.nprocess];
        timeLastArrival = new int[infra.nprocess];
    }

    /**
     * this method computes the mean time between sending.
     * @return
     */
    public double senseOverhead(){
        /*
         * sensing the number of message receive by a process at each TC
         * time interval.
         */
        double overhead = ((double)numberOfCtlMsg / (double)numberOfMsg);
        return overhead;
/*        if(meanOverhead < 0) meanOverhead = overhead;

        meanOverhead = 0.8 * meanOverhead + 0.2 * overhead;

        return meanOverhead;*/

    }

    public double senseBlocking(){
        if(numberOfMsgDeliveried == 0) return 0.0;
        return meanBlocking/1000.0;
        //return ((double)(totBlockingTime/1000.0) / (double) numberOfMsgDeliveried);
    }

    public void actuate(double value){
        //setTimeSilence((new Integer((int)value)).toString());
        ts = (int) value;
    }

    public double sense() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void control(){
           double ctrlOverhd = controlOverhead(
                senseOverhead()
           );

           double ctrlBlockg = controlBlocking(
                senseBlocking()
           );

           double TS = ((double) ts)/1000.0 + ctrlOverhd  + ctrlBlockg ;

           if(TS > TSMaximun * 1000.0) TS = TSMaximun * 1000.0;
           if(TS < TSMinimum * 1000.0) TS = TSMinimum * 1000.0;

           actuate(TS);

    }

    public int estimaTSMax() {
        int max = ts;
        for (int i = 0;i<infra.nprocess;i++) {
            if (max < meanTimeBetweenArrival[i])
                    max = meanTimeBetweenArrival[i];
        }
        return (int)  (max*1.1);
    }

    public double controlOverhead(double value){
        double error = value - refOverhead;
        ctrlOverhead += 5 * error;

        double signal = ctrlOverhead * wOvhd;

        if(signal > TSMaximun *  1000.0) signal = TSMaximun * 1000.0;
        if(signal < TSMaximun * -1000.0) signal = TSMaximun * -1000.0;
        if(wOvhd > 0) ctrlOverhead = signal / wOvhd;
        return signal;
    }

    public double controlBlocking(double value){
        double error = refBlocking - value;
        ctrlBlocking += 5 * error;

        double signal = ctrlBlocking * wBlck;

        if( signal > TSMaximun * 1000.0) signal = TSMaximun * 1000.0;
        if(signal < TSMaximun * -1000.0) signal = TSMaximun * -1000.0;
        if(wBlck > 0) ctrlOverhead = signal / wOvhd;
        return signal;

    }


    @Override
    public void deliver(Mensagem msg) {
        super.deliver(msg);
        int t1 = (int)infra.clock.value();
        Integer t0 = buffer.get(msg.getId());

        numberOfMsgDeliveried++;

        if(t0!=null)
        {

            double blockingTime = (double)(t1 - t0.intValue());

            if(meanBlocking < 0) meanBlocking = blockingTime;

            meanBlocking = meanBlocking * 0.9 + 0.1 * blockingTime;

            //totBlockingTime += blockingTime;

            buffer.remove(msg.getId());
        }

        control();

    };

    @Override
    public void receive(Mensagem msg) {
        super.receive(msg);
        numberOfMsg ++;
        // Modificação para estimar ts
        if ( (msg.tipo == TIMEDCB_APP) || (msg.tipo == TIMEDCB_TS) ) {
            meanTimeBetweenArrival[msg.remetente] =
                msg.relogioFisico - timeLastArrival[msg.remetente];
            timeLastArrival[msg.remetente]=msg.relogioFisico;
            // tsmax = estimaTSMax();
        };

        if(msg.tipo != TIMEDCB_APP)
            numberOfCtlMsg ++;

//        control();

        Integer t0 = new Integer((int)infra.clock.value());
        buffer.put(msg.getId(), t0);

    }


}
