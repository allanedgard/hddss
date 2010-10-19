
import java.util.Hashtable;

/*
 * To change this template, choose Tools | Templates
 * and open the template nic_in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class AgenteAutonomicTimedCB3 extends AgenteTimedCB implements Controller{
    double MTBS = ts;
    double TSMaximun = 0.2;
    double TSMinimum = 0;
    double TimeSilence = ts;

    int numberOfCtlMsgSending = 0;
    int numberOfMsgSending = 0;

    double refOverhead = 0.1;
    double refBlocking = 50.0/1000.0;

    double blockingRelevance = 0.5;
    double overheadRelevance = 0.5;

    int numberOfMsgDeliveried = 0;
    double totBlockingTime = 0;
    
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Hashtable<String, Integer> buffer = new Hashtable<String, Integer>();
    
    public AgenteAutonomicTimedCB3() {
        super();
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

        return ((double)numberOfCtlMsgSending / (double)numberOfMsgSending);

    }

    public double senseBlocking(){
        return ((double)(totBlockingTime/1000.0) / (double) numberOfMsgDeliveried);
    }
    
    public void actuate(double value){
        //setTimeSilence((new Integer((int)value)).toString());
        ts = (int) value;
    }

    public double sense() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void control(){
           double tsOverhead = controlOverhead(
                senseOverhead()
           );

           double tsBlocking = controlBlocking(
                senseBlocking()
           );

           double wOvhd = overheadRelevance / (overheadRelevance + blockingRelevance);
           double wBlck = blockingRelevance / (overheadRelevance + blockingRelevance);

           double TS = tsOverhead * wOvhd + tsBlocking * wBlck;

           if(TS > TSMaximun * 1000.0) TS = TSMaximun * 1000.0;
           if(TS < TSMinimum * 1000.0) TS = TSMinimum * 1000.0;
           
           actuate(TS);

    }

    public double controlOverhead(double value){
        double error = value - refOverhead;
        //it's translate milliseconds to seconds. I think it's true. CHECK
        TimeSilence = ts/1000.0;
        TSMaximun = tsmax/1000.0;

        //We must decide the margin later! 0.1 is a gess!!! CHECK
        TimeSilence = TimeSilence + 0.1 * error;

//        if(TimeSilence > TSMaximun)
//            TimeSilence = TSMaximun;
//        else if(TimeSilence < TSMinimum)
//            TimeSilence = TSMinimum;

        return TimeSilence * 1000.0;
    }

    public double controlBlocking(double value){
        double error = refBlocking - value;
        //it's translate milliseconds to seconds. I think it's true. CHECK
        TimeSilence = ts/1000.0;
        TSMaximun = tsmax/1000.0;

        //We must decide the margin later! 0.1 is a gess!!! CHECK
        TimeSilence = TimeSilence + 0.1 * error;

//        if(TimeSilence > TSMaximun)
//            TimeSilence = TSMaximun;
//        else if(TimeSilence < TSMinimum)
//            TimeSilence = TSMinimum;

        return TimeSilence * 1000.0;
    }



    @Override
    public void enviaMensagemGrupo(int clock, int tipo, Object valor, int LC) {

        numberOfMsgSending ++;

        if(tipo == TIMEDCB_TS){

            numberOfCtlMsgSending ++;

        }
        //I put it here but I'm not sure. (CHECK)
       // control();
        super.enviaMensagemGrupo(clock, tipo, valor, LC);
    }

    @Override
    public void enviaMensagemGrupo(int clock, int tipo, Object valor, int LC, boolean pay) {

        numberOfMsgSending ++;

        if(tipo == TIMEDCB_TS){

            numberOfCtlMsgSending ++;

        }

        //I put it here but I'm not sure. (CHECK)
        //control();
        super.enviaMensagemGrupo(clock, tipo, valor, LC, pay);
    }

    @Override
    public void deliver(Mensagem msg) {
        super.deliver(msg);
        int t1 = (int)infra.clock.value();
        Integer t0 = buffer.get(msg.getId());

        numberOfMsgDeliveried++;

        if(t0!=null)
        {

            int blockingTime = t1 - t0.intValue();
            totBlockingTime += blockingTime;

            buffer.remove(msg.getId());
        }

        control();
        
    }

    @Override
    public void receive(Mensagem msg) {
        super.receive(msg);
        Integer t0 = new Integer((int)infra.clock.value());
        buffer.put(msg.getId(), t0);
    }


}
