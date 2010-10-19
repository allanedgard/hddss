/**
 *
 * @author allan
 */
public abstract class CanalComOmissao extends Channel {
    
    Randomico r;
    double prob;
    
    CanalComOmissao (double p) {
        r = new Randomico();
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
