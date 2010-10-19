public class CanalDeterministicoFaixa extends Channel {
    
    int delay_min;
    int delay_max;
    Randomico r;
    
    CanalDeterministicoFaixa () {
        r = new Randomico ();
    }

     public void setDeltaMaximo(String dt) {
            delay_max = Integer.parseInt(dt);
     }

     public void setDeltaMinimo(String dt) {
            delay_min = Integer.parseInt(dt);
     }

    
    int atraso() {
        return r.irandom(delay_min, delay_max);
    }
    
    boolean status() {
        return true;
    }
}
