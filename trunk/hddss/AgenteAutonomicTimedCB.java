
import java.util.Hashtable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * it controls itself
 * @author aliriosa
 */
public class AgenteAutonomicTimedCB extends AgenteTimedCB implements Controller{
    double MTBS = ts;
    double TSMaximun = 0.2;
    double TSMinimum = 0;
    double TimeSilence = ts;
    int numberOfCtlMsgSending = 0;
    int numberOfMsgSending = 0;
    double refOverhead = 0.8;
    @SuppressWarnings("UseOfObsoleteCollectionType")
    static TSCollection TSC = new TSCollection();
    public AgenteAutonomicTimedCB() {
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
        
        return ((double)numberOfCtlMsgSending / (double)numberOfMsgSending);
        
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
        double error = value - refOverhead;
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


    
}
