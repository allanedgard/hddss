

public class Agent extends Thread {
    int id;
    char tipo;
    Context contexto;
    boolean done;

    static final String TAG = "agent";
    RuntimeContainer infra;
    
    Agent() {
        done = false;
    }

    /**
     * this method is used to perform particular configurations in the agent.
     */
    public void setup() {
    }

    public void setId(int i){
        id = i;
    }

    public void setType(char tp){
        tipo = tp;
    }

    public final void init() {
        setup();
    }
 

   public final boolean status() {
        if (infra.faultModel == null) {
            return true;
        } else
        return infra.faultModel.status();
    }

    
    @Override
    public final void run() {

        infra.start();
        
    }
    
   public void send(Message m){
       infra.nic_out.adiciona((int)infra.clock.value(), m);

   }
    public void startup(){
/*        int temp[] = {11, 22,33, 44, 55};
        for (int i = 0; i<temp.length; i++) {
            System.out.println("avanca");
            send(new Mensagem());
            //this.criamensagem(temp[i], this.id, infra.nprocess, i, "teste",0);
        }*/
    }
    
    public void execute() {
        /*
         *   Este evento pode ser sobrecarregado pela ação específica
         *   do protocolo
         */
    }
    
    public void receive(Message msg) {
        /* 
         *   Este evento pode ser sobrecarregado pela ação específica 
         *   do protocolo
         */
        infra.app_in.adiciona((int)this.infra.clock.value(), msg);
    }
    
    
    public void deliver(Message msg) {
        /*
         *   Este evento pode ser sobrecarregado pela ação específica
         *   do protocolo
         */
    }
        
}
