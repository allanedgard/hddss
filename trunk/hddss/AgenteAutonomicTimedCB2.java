
import java.util.Hashtable;

/*
 * To change this template, choose Tools | Templates
 * and open the template nic_in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class AgenteAutonomicTimedCB2 extends AgenteTimedCB implements Controller{
    double MTBS = ts;
    double TSMaximun = 0.2;
    double TSMinimum = 0;
    double TimeSilence = ts;
    int numberOfMsgDeliveried = 0;
    double meanBlockingTime = 0;
    double totBlockingTime = 0;
    double refBlockingTime = (double)50.0/1000.0;
    double receptionTime = 0;
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Hashtable<String, Integer> buffer = new Hashtable<String, Integer>();
    
    public AgenteAutonomicTimedCB2() {
        super();
    }

    /**
     * this method computes the mean time between sending.
     * @return
     */
    public double sense(){
        /*
         * sensing the number of message receive by a process at each TC
         * time interval.
         */

        return ((double)(totBlockingTime/1000.0) / (double) numberOfMsgDeliveried);

    }

    public void actuate(double value){
        //setTimeSilence((new Integer((int)value)).toString());
        ts = (int) value;
    }

    public void control(){
        actuate(
           control(
                sense()
           )
        );
    }

    public double control(double value){
        double error = refBlockingTime - value;
        //it's translate milliseconds to seconds. I think it's true. CHECK
        TimeSilence = ts/1000.0;
        TSMaximun = tsmax/1000.0;

        //We must decide the margin later! 0.1 is a gess!!! CHECK
        TimeSilence = TimeSilence + 0.1 * error;

        if(TimeSilence > TSMaximun)
            TimeSilence = TSMaximun;
        else if(TimeSilence < TSMinimum)
            TimeSilence = TSMinimum;

        return TimeSilence * 1000.0;
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

            control();

        }
        
    }

    @Override
    public void receive(Mensagem msg) {
        super.receive(msg);
        Integer t0 = new Integer((int)infra.clock.value());
        buffer.put(msg.getId(), t0);
    }



/*    @Override
    public void enviaMensagemGrupo(int clock, int tipo, Object valor, int LC) {

        numberOfMsgSending ++;

        if(tipo == TIMEDCB_TS){

            numberOfCtlMsgSending ++;

        }
        //I put it here but I'm not sure. (CHECK)
        control();
        super.enviaMensagemGrupo(clock, tipo, valor, LC);
    }

    @Override
    public void enviaMensagemGrupo(int clock, int tipo, Object valor, int LC, boolean pay) {

        numberOfMsgSending ++;

        if(tipo == TIMEDCB_TS){

            numberOfCtlMsgSending ++;

        }

        //I put it here but I'm not sure. (CHECK)
        control();
        super.enviaMensagemGrupo(clock, tipo, valor, LC, pay);
    }

    @Override
    public void deliver(Mensagem msg) {
        super.deliver(msg);
        //control();
    }

*/

}
