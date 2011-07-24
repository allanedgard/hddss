package br.ufba.lasid.jds.prototyping.hddss;

public class Agent_CristianPgc extends SimulatedAgent {
        int lastClock;
        double somaClocks;
        double numeroClocks;
        double prob;
        int DELTA;
        int LogicalClock;
        int Clock_Renewal_Time;
        int CRT; 
        final int PG_APP = 1;
        final int PG_NEW_GROUP = 2;
        final int PG_PRESENT = 3;
        final int CK_REQ = 255;
        final int CK_REP = 254;
        int resiliencia;
        int [] membership;
        int Renewal_Time;
        int pi;
        Randomize r;
        java.util.ArrayList msgRecebidas;
        
        Agent_CristianPgc() {
        super();

        }

        public void setup() {
            membership = new int[infra.nprocess];
            Renewal_Time = -1;
            r = new Randomize(ID);
            LogicalClock = 0;
            msgRecebidas = new java.util.ArrayList();
            resiliencia = 0;
            CRT = 0;
        }

        public void setDelta(String dt) {
            DELTA = Integer.parseInt(dt);
        }

        public void setResiliencia(String r) {
            resiliencia = Integer.parseInt(r);
        }
        
        public void setPi(String p) {
            pi = Integer.parseInt(p);
        }

        public void setRenewalTime (String rt) {
            Clock_Renewal_Time = Integer.parseInt(rt);
            CRT = Clock_Renewal_Time;
        }

        public void setProbabilidadeGerarPacote (String po) {
            prob = Double.parseDouble(po);
        }

    @Override
        public void startup(){
            for (int i = 0;i<infra.nprocess;i++) {
                membership[i]=0;
            }
            int clock = (int)infra.clock.value();
            this.createMessage(clock, this.ID, infra.nprocess, PG_NEW_GROUP, new Content_PGC(clock+DELTA, ID), -1 );
        }
    
        public boolean[] visao() {
            int max=0;
            for (int i = 0;i<infra.nprocess;i++) {
                if (membership[i]> max) max = membership[i];
            }
            boolean [] vs = new boolean[infra.nprocess];
            for (int i = 0;i<infra.nprocess;i++) {
                if (membership[i]== max) {
                    vs[i]= true; }
                else
                    vs[i]= false;
            }
            return vs;
        }
        
        public void execute() {
            boolean [] vs = visao();
            int clock = (int)infra.clock.value();
            int tick  = (int)infra.clock.tickValue();
            double ro = infra.context.get(RuntimeSupport.Variable.ClockDeviation).<Double>value();
            infra.debug("p"+ID+": clock="+clock);
            if (r.uniform() <= prob) {
                LogicalClock ++;
                this.createMessage(clock, this.ID, infra.nprocess, PG_APP, "payload", LogicalClock, true );
            }
            if (clock == Renewal_Time) {
                Renewal_Time = -1;
                this.createMessage(clock, this.ID, infra.nprocess, PG_PRESENT, new Content_PGC(clock, ID), -1);
                Renewal_Time = clock + pi;
            }
            if (clock == CRT) {
                infra.debug("p"+ID+": sinc, clock="+clock);
                Content_Sync sc = new Content_Sync(clock + ro * tick);
                lastClock++;
                this.createMessage(clock,this.ID,infra.nprocess,CK_REQ,sc,lastClock);
                somaClocks = 0.0;
                numeroClocks = 0;
                CRT = clock + Clock_Renewal_Time;
            }
            
        }

        public void deliver(Message msg) {
            if (msg.type == PG_APP)
                   Simulator.reporter.stats("blocking time",
                           (int)infra.clock.value()-msg.receptionTime);
            if (msg.logicalClock > LogicalClock) {
                LogicalClock = msg.logicalClock+1;
            }
            else {
                LogicalClock ++;
            }
        }
        
        public void receive(Message msg) {
            /* 
            *   Este evento pode ser sobrecarregado pela ação específica 
            *   do protocolo
            */
            int V;
            int M;
            int clock = (int)infra.clock.value();
            int tick  = (int)infra.clock.tickValue();
            double DESVIO = infra.context.get(RuntimeSupport.Variable.MaxDeviation).<Double>value();
            double ro = infra.context.get(RuntimeSupport.Variable.ClockDeviation).<Double>value();
            Network network = infra.context.get(RuntimeSupport.Variable.Network).<Network>value();
            Content_Sync sc;
            switch (msg.type) {
                case PG_NEW_GROUP: 
                    V = ( (Content_PGC) msg.content).V;
                    M = ( (Content_PGC) msg.content).M;
                    // if (V <= clock) {
                        this.createMessage(V, this.ID, infra.nprocess, PG_PRESENT, new Content_PGC(V, ID), 0 );
                        Renewal_Time = V+pi;
                    // }                    
                    break;
                case PG_PRESENT:
                    V = ( (Content_PGC) msg.content).V;
                    M = ( (Content_PGC) msg.content).M;
                    membership[M] = V; 
                    break;
                case PG_APP:
                    /* Alterado para incluir Flood
                     * 
                     */
                    int t_e = msg.physicalClock + (int) DESVIO + (1+resiliencia) * DELTA;
                    if (msgRecebidas.contains(msg.getId()) == false) {
                        infra.debug("p"+ID+": relayed from "+msg.sender);
                        msgRecebidas.add(msg.getId());
                        if (msg.hops < resiliencia) {
                            msg.hops++;
                            for (int i=0; i< infra.nprocess; i++) {
                                if (  (i != ID) && (i != msg.relayFrom) )
                                relayMessage(clock, msg, i);
                            }
                        };
                        infra.app_in.add(t_e, msg);
                    } else {
                        network.unicasts[msg.type]++;
                    }                        
                    // (int r, int d, int t, int rL, int rF, Object c);
                    // Original  int t_e = msg.relogioFisico + (int) controle.DESVIO + DELTA;
                    // Entrega.adiciona(t_e, msg);
                    break;
                case CK_REQ:
                    sc = (Content_Sync) msg.content;
                    sc.atual = clock + ro * tick;
                    this.createMessage(clock,this.ID,msg.sender,CK_REP,sc, msg.logicalClock);
                    break;
                case CK_REP:
                    double agora = clock + ro * tick;
                    sc = (Content_Sync) msg.content;
                    if (msg.logicalClock == lastClock) {
                        //System.nic_out.println("CLK");
                        numeroClocks++;
                        somaClocks += sc.atual+(agora-sc.inicio)/2;
                        if (numeroClocks == infra.nprocess) {
                            infra.debug("p"+ID+" clock atual:"+clock+" tick:"+tick);
                            double ajusta = somaClocks/numeroClocks;
                            double atual = clock + ro * tick;
                            somaClocks = .0;
                            numeroClocks=0;
                            if (ajusta < atual){
                                infra.clock.adjustCorrection((int) ( (atual - ajusta)*(1/ro) ));
                            }
                            else {
                                infra.clock.adjustValue((int)ajusta);
                                infra.clock.adjustTickValue((int) (((ajusta - (int) ajusta)) * (1/ro)));
                            }
                        }
                    }
                    break;
                    
            }
            
        }
        
}
