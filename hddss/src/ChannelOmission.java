/**
 *
 * @author allan
 */
public abstract class ChannelOmission extends Channel {
    
    Randomize r;
    double prob;
    
    ChannelOmission (double p) {
        r = new Randomize();
        prob=p;
    }    




    abstract int atraso();
    
    boolean status() {
        if (r.uniform() <= prob) {
                return false;
            }
        else return true;
    } 
    
    
}
