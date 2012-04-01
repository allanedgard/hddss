package br.ufba.lasid.jds.prototyping.hddss;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

public abstract class Network extends Thread{
    Buffer net_in;                  //  BUFFER DE MENSAGENS DE ENTRADA NA REDE
    private Simulator container;    //  SIMULADOR É O CONTAINER
    Channel channels[][];           //  GRAFO DE CANAIS ENTRE PROCESSOS
    long[][] delaymap;              //  MAPA DE ULTIMOS ATRASOS ENTRE CANAIS PARA FIFO
    private boolean fifo = false;   //  INDICA SE HÁ COMPORTAMENTO FIFO DA REDE
    private long next = 0;          //            
    private long last = 0;          //  
    private double tqueue = 0.0;    //
    private boolean done = false;   //
    Scenario scenario;              //  SCENARIO DA REDE
    AbstractClock networkClock;      //  RELOGIO DA REDE
    
    static final String TAG = "network";

    public int broadcasts[];
    public int unicasts[];
    public int multicasts[];

    
    Network(){
        net_in = new Buffer();
        broadcasts = new int[256];
        unicasts = new int[256];
        multicasts = new int[256];
        networkClock = new Clock_Virtual();
    }
    
    public final boolean hasDone() {
        return done;
    }
    
    public final void setDone(boolean d) {
        done=d;
    }
    
    public final boolean verifyChannel(int i, int j) {
        /*
         *  RETORNA O ESTADO DE UM CANAL (OK=TRUE, NAO=FALSO)
         *  SE O CANAL NAO ESTIVER NEGOCIADO RETORNA FALSO
         */
        if (channels[i][j] == null) {
            return false;
        }
        return channels[i][j].status();

    }

    public final void setScenario(Scenario s) {
        /*
         *  ATRIBUI O SCENARIO DE NETWORK
         *  UTILIZADO PARA REGISTRO DAS ESTATISTICAS E
         *  E NEGOCIACAO DO SIMULADOR
         */
        scenario = s;
    }
    
    public final void handshaking(int p_i, int p_j) {
        /*
         *  NEGOCIA OS CANAIS ENTRE PROCESSOS
         */
        try {
            String TAG = Channel.TAG + "["+p_i+"]["+p_j+"]";

            if(!container.config.getString(TAG, "null").equals("null"))
            {
                channels[p_i][p_j] = (Channel)Factory.create(TAG, Channel.class.getName());
                channels[p_i][p_j].connect(container.scenario.p[p_i], container.scenario.p[p_j]);
                Factory.setup(channels[p_i][p_j], TAG);
            }
            else
            {
                channels[p_i][p_j] = (Channel)Factory.create(Channel.TAG, Channel.class.getName());
                channels[p_i][p_j].connect(container.scenario.p[p_i], container.scenario.p[p_j]);
                Factory.setup(channels[p_i][p_j], Channel.TAG);
            }


        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    /*  #scenarios
     *  included
     */
    public final void handshaking(int p_i, int p_j, int type) {
        /*
         *  UTILIZADO PARA RECONFIGURACAO DE CANAIS DE UM CENARIO 
         *  PRE-ESTABELECIDO PARA OUTRO
         */
        try {
            String TAG = Channel.TAG + "["+type+"]";

            channels[p_i][p_j] = (Channel)Factory.create(TAG, Channel.class.getName());
            channels[p_i][p_j].connect(container.scenario.p[p_i], container.scenario.p[p_j]);
            Factory.setup(channels[p_i][p_j], TAG);

        } catch (Exception e) {
                e.printStackTrace();
        }
    }


    public final void init(Simulator cxt){
        /* 
         *  INICIA A CONFIGURACAO DE NETWORK
         */
        container = cxt;
        int n = cxt.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value().intValue();
        channels = new Channel[n][n];
        // totalticks = (int)(1/container.ro);
    }

    public synchronized final void increaseTick() {
        /*
         *  AO AVANCAR UM PASSO NA SIMULACAO, A REDE 
         *  AVANÇA O RELOGIO E VERIFICA BUFFERs DE SAIDA
         */
        this.clockTick();
        outcoming();
    }

    @Override
    public final void run() {

        while (true) {
            this.increaseTick();
            this.yield();
            if (done)
                break;
        }
        
    }

    public synchronized final void clockTick() {
        /*
         *  AVANCA O CLOCK DO RELOGIO DA REDE
         */
            ((Clock_Virtual)networkClock).tick();
            this.yield();
    }

    public void outcoming(){
      long clock =  networkClock.value();
      while(true){
         ArrayList buffer = net_in.getMsgs(clock);
         if(buffer.isEmpty()){
            break;
         }

         Message m = (Message)buffer.get(0);
         proc(m);

         propagate(m);

      }
    }
    
    public void send(Message msg){
        /*
         *  AO RECEBER UMA MENSAGEM, A ESCALONA NUMA
         *  FILA DE MENSAGENS 
         * 
         */
        synchronized(this){
           incoming(msg);
        }
       
    }

    public void proc(Message msg) {
        /*
         *  CALCULA O TEMPO NA FILA E O ADICIONA AO ATRASO DA MENSAGEM
         */
         long clock =  networkClock.value();
         long dt = clock - last;
         last = clock;
         tqueue -= dt;
         tqueue = (tqueue < 0? 0: tqueue);
         //        long at = delay();
         tqueue += delay(msg);
         //conteiner.get(RuntimeSupport.Variable.QueueDelayTrace).<DescriptiveStatistics>value().addValue(tqueue);
         scenario.reporter.stats("network queue delay", tqueue);
       
    }

    /* Simulate the travel of the message from agent to router network */
    double tripdelayBalance = 0.5;
    double inbalance(){
        /* 
         *  RETORNA O PESO DO TEMPO DE ATRASO DO CANAL ANTES DO ENFILEIRAMENTO
         */
        return tripdelayBalance;
    }

    double outbalance(){
        /* 
         *  RETORNA O PESO DO TEMPO DE ATRASO DO CANAL APOS O ENFILEIRAMENTO
         */
       return (1 - tripdelayBalance);
    }

    private long tripdelay(int p_i, int p_j, double balance, Message m){
        /*
         *  CALCULA O ATRASO DE ACORDO COM O BALANCEAMENTO
         *  O CALCULO DO ATRASO DO CANAL É FEITO UMA ÚNICA VEZ
         */
         long calculatedDelay;
         if (m.calculatedDelay==0) {
                calculatedDelay = channels[p_i][p_j].delay();
                m.calculatedDelay = calculatedDelay;
         }  else {
                calculatedDelay = m.calculatedDelay;
         }
         long delay = (long)Math.round(calculatedDelay * balance);
         return delay;
    }
    
    protected long calcTimestamp(int p_i, int p_j, double balance, Message m){
        /*
         *  CALCULA O TIMESTAMP PARA INSERCAO NA FILA
         *  SE O CANAL FOR FIFO, ESSE TIMESTAMP SEMPRE SERA SUPERIOR AO DO
         *  ULTIMO TIMESTAMP
         */
         long clock = scenario.globalClock.value();
         long timestamp = clock + tripdelay(p_i, p_j, balance, m);

         /* if network consider fifo service then the messages will arrive in fifo order */
         if(fifo){
            if(delaymap == null){
               int n = container.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value();
               delaymap = new long[n][n];
            }

            if(timestamp < delaymap[p_i][p_j]){
               timestamp = delaymap[p_i][p_j] + 1;
            }

            delaymap[p_i][p_j] = timestamp;
         }


         return timestamp;
    }


   private void incoming(Message m){
       /*
        *   INSERE MENSAGEM NO BUFFER ADEQUADO
        */
       if(isLoopback(m)){
           /*
            *   SE FOR LOOPBACK INVOCA lookback
            */
          loopback(m);
          return;
       }
       
       int p_i = m.sender;
       int p_j = m.destination;

       if(isMulticast(m)){
            p_j = p_i;
       }

       if(isBroadcast(m)){
            p_j = p_i;
       }

       /*   INTERPRETACAO INCORRETA DO RELAY
       if(isRelay(m)){
            p_i = m.relayFrom;
            p_j = m.relayTo;
       }
        */

       /*
        *   ENFILEIRA A MSG PARA RECEIVE CONFORME OS PROCESSOS ENVOLVIDOS
        */
       net_in.add(calcTimestamp(p_i, p_j, inbalance(), m), m);

    }
        
    public void propagate(Message msg){
      synchronized(this){
         if(isMulticast(msg)){
            multicast(msg);
            return;
         }

         if(isRelay(msg)){
            relay(msg);
            return;
         }

         if(isBroadcast(msg)){
            broadcast(msg);
            return;
         }//end if isBroadcast
         
         unicast(msg);
      }
    }
    public void loopback(Message msg){
        /*
         *  SE A MENSAGEM FOR DE loopback o ENVIO É 
         *  AGENDADO PARA O PRÓXIMO clock
         */
        synchronized(this){
            scenario.reporter.count("network loopbacks");

            if(msg.type >=0) scenario.reporter.count("network loopbacks class " + msg.type);

            int address = msg.sender;
            /*  #scenario
             *  Agent p = conteiner.p[address];
             */
            Agent p = container.scenario.p[address];
            
            //p.getInfra().nic_in.add((int)(p.getInfra().cpu.value())+1, msg);
            // ALTERADO PARA RELOGIO GLOBAL
            p.getInfra().nic_in.add((int)(scenario.globalClock.value())+1, msg);
        }
    }
    
    public boolean isMulticast(Message msg){
       return msg.multicast;
    }
    public boolean isLoopback(Message msg){
        return isLoopback(msg.sender, msg.destination) && !msg.multicast;
    }

    public boolean isLoopback(int p_i, int p_j){
        return (p_i == p_j);
    }

    public boolean isBroadcast(Message msg){
        int n = container.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value().intValue();
        return (msg.destination == n) && !msg.multicast;
    }

    public boolean isRelay(Message msg){
        return (msg.relayTo != -1) && !msg.multicast;
    }

    public void relay(Message msg){
        /*
         *  FAZ O RELAY
         *  O CODIGO DE RELAY TEM QUE SER REVISADO
         */
        synchronized(this){

            scenario.reporter.count("network relays");

            if(msg.type >=0) scenario.reporter.count("network relays class " + msg.type);

            int p_i = msg.relayFrom;
            int p_j = msg.relayTo;

            if(verifyChannel(p_i, p_j)) transfer(p_i, p_j, msg);
        }
    }
    
    public void unicast(Message msg){
        /*
         *  FAZ A ENTREGA UNICAST, VERIFICANDO O ESTADO DO CANAL E 
         *  TRANSFERINDO
         */
        synchronized(this){

            scenario.reporter.count("network unicasts");

            if(msg.type >=0) scenario.reporter.count("network unicasts class " + msg.type);

            //unicasts[msg.type]++;
            if (isLoopback(msg)) {
                loopback(msg);
                return;
            }
            if (verifyChannel(msg.sender, msg.destination))
                transfer(msg.sender, msg.destination, msg);
        }
    }

    public void multicast(Message msg){
        /*
         *  OBTEM TODOS OS MEMBROS DO GRUPO
         *  E PARA CADA MEMBRO FAZ A ENTREGA
         */
      synchronized(this){
         NetworkGroup g = gtable.get(msg.destination);
         if(g != null){
            
            scenario.reporter.count("network multicasts");

            if(msg.type >=0) scenario.reporter.count("network multicasts class " + msg.type);

            int p_i = msg.sender;
            
            for(int p_j : g){
               if (p_i != p_j && verifyChannel(p_i, p_j)){
                  transfer(p_i, p_j, msg);
               }
               else if (p_i == p_j && verifyChannel(p_i, p_j)) {
                   loopback(msg);
               }
            }
         }
      }
    }

    public void transfer(int p_i, int p_j, Message msg){
        /*
         *  FAZ A TRANSFERENCIA
         *  AJUSTA O TEMPO CALCULADO COM O DESINCRONISMO NO RELOGIO DO RECEPTOR
         */
        synchronized(this){
            long delay =  (long) (tripdelay(p_i, p_j, outbalance(), msg)+ tqueue);
            
            /*
            double ticksAdj = (((Clock_Virtual) scenario.p[p_j].getInfra().clock).rho);
            double totalTicks = Clock_Virtual.getNTicks();
            long adj = (long) ((msg.calculatedDelay+tqueue)* ( ((totalTicks+ticksAdj)/totalTicks) -1.0 ) );
            */
            
            channels[p_i][p_j].deliverMsg(msg, delay);
        }
    }

    public void broadcast(Message msg){
        synchronized(this){
            int n = container.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value();

            scenario.reporter.count("network broadcasts");
            
            if(msg.type >=0) scenario.reporter.count("network broadcasts class " + msg.type);

            int p_i = msg.sender;

            for (int p_j=0; p_j < n; p_j++) {

                if (isLoopback(p_i, p_j)){

                    loopback(msg);

                }else if (verifyChannel(p_i, p_j)){

                        transfer(p_i, p_j, msg);

                }
            }
        }
    }
    
    public final void debug(String d) {
        /*
         *  SE DEBUG ESTIVER ATIVO, REGISTRA INFO DE DEBUG
         */
        boolean debug = container.get(RuntimeSupport.Variable.Debug).<Boolean>value();
        if (debug)
            container.out.println(d);
    }

    public final void setFIFO(String fifo){
        /*
         *  DEFINE SE O COMPORTAMENTO DOS CANAIS É FIFO OU NAO
         */
        this.fifo = Boolean.parseBoolean(fifo);
    }

    public final void setTripBalance(String b){
        /*
         *  DEFINE O BALANCO ENTRE P_i E A REDE E A REDE E P_j
         */
       tripdelayBalance = Double.parseDouble(b);
       if(tripdelayBalance <= 0) tripdelayBalance = 0.001;
       if(tripdelayBalance > 1) tripdelayBalance = 0.5;
    }

    abstract double delay(Message m);
    /*
     *  ATRASO DA REDE DEPENDE DA REDE ESPECIFICA
     */

    public void setGroups(String gdefs){
        /*
         *  DEFINE GRUPOS DE PROCESSOS
         */
      gdefs = gdefs.replace('[', ' ');
      gdefs = gdefs.replace(']', ' ');
      gdefs = gdefs.trim();
      
      StringTokenizer tokens = new StringTokenizer(gdefs);
      
      //NetworkGroup group = new NetworkGroup();
      while(tokens.hasMoreTokens()){
         String gtoken = tokens.nextToken(";");
         gtoken = gtoken.trim();
         //debug(gtoken);
         setGroup(gtoken);
      }
    }

    public void setGroup(String gdef){
        /*
         *  DEFINE UM GRUPO DE PROCESSOS
         */
      gdef = gdef.replace('[', ' ');
      gdef = gdef.replace(']', ' ');
      gdef = gdef.trim();
      String[] gparts = gdef.split("\\)");
      
      if(gparts != null && gparts.length == 2){
         String gtoken = gparts[0];
         String hstoken = gparts[1];

         gtoken = gtoken.trim();
         gtoken = gtoken.substring(2);
         gtoken = gtoken.trim();
         
         int gid = Integer.valueOf(gtoken);
         
         NetworkGroup g = gtable.get(gid) ;

         if(g == null){
            g = new NetworkGroup(gid);
            gtable.put(gid, g);
         }

         hstoken = hstoken.trim();
         hstoken = hstoken.replace('{', ' ');
         hstoken = hstoken.replace('}', ' ');
         hstoken = hstoken.trim();
         StringTokenizer tokens = new StringTokenizer(hstoken, ",");
         while(tokens.hasMoreTokens()){

            String htoken = tokens.nextToken(",");
            htoken = htoken.trim();

            int h = Integer.valueOf(htoken);

            if(!g.contains(h)){
               g.add(h);
            }
         }
      }    
    }

    /*
     *  UTILIZADO PARA DEFINIR UM GRUPO DE REDE
     */
    NetworkGrouptable gtable = new NetworkGrouptable();
    
    class NetworkGrouptable extends Hashtable<Integer, NetworkGroup>{
       
    }

    class NetworkGroup extends ArrayList<Integer>{
       int groupid;

      public NetworkGroup() {
         this.groupid = -1;
      }
      public NetworkGroup(int groupid) {
         this.groupid = groupid;
      }       
    }
}
