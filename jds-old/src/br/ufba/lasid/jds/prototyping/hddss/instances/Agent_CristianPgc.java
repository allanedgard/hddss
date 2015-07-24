package br.ufba.lasid.jds.prototyping.hddss.instances;

import br.ufba.lasid.jds.prototyping.hddss.Message;
import br.ufba.lasid.jds.prototyping.hddss.Network;
import br.ufba.lasid.jds.prototyping.hddss.Randomize;
import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport;
import br.ufba.lasid.jds.prototyping.hddss.SimulatedAgent;
import br.ufba.lasid.jds.prototyping.hddss.Simulator;

public class Agent_CristianPgc extends SimulatedAgent {
        int lastClock;
        double somaClocks;
        double numeroClocks;
        double prob;
        int DELTA;
        int LogicalClock;
        int Clock_Renewal_Time;
        long CRT; 
        final int PG_APP = 1;
        final int PG_NEW_GROUP = 2;
        final int PG_PRESENT = 3;
        final int CK_REQ = 255;
        final int CK_REP = 254;
        int resiliencia;
        long [] membership;
        long Renewal_Time;
        int pi;
        Randomize r;
        java.util.ArrayList msgRecebidas;
        
        Agent_CristianPgc() {
        super();

        }

        public void setup() {
            membership = new long[getInfra().nprocess];
            Renewal_Time = -1;
            r = new Randomize(getAgentID());
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
            for (int i = 0;i<getInfra().nprocess;i++) {
                membership[i]=0;
            }
            int clock = (int)getInfra().clock.value();
            this.createMessage(clock, this.getAgentID(), getInfra().nprocess, PG_NEW_GROUP, new Content_PGC(clock+DELTA, getAgentID() ), -1 );
        }
    
        public boolean[] visao() {
            long max=0;
            for (int i = 0;i<getInfra().nprocess;i++) {
                if (membership[i]> max) max = membership[i];
            }
            boolean [] vs = new boolean[getInfra().nprocess];
            for (int i = 0;i<getInfra().nprocess;i++) {
                if (membership[i]== max) {
                    vs[i]= true; }
                else
                    vs[i]= false;
            }
            return vs;
        }
        
        public void execute() {
            boolean [] vs = visao();
            long clock = getInfra().clock.value();
            long tick  = getInfra().clock.tickValue();
            double ro = getInfra().context.get(RuntimeSupport.Variable.ClockDeviation).<Double>value();
            getInfra().debug("p"+getAgentID()+": clock="+clock);
            if (r.uniform() <= prob) {
                LogicalClock ++;
                this.createMessage(clock, this.getAgentID(), getInfra().nprocess, PG_APP, "payload", LogicalClock, true );
            }
            if (clock == Renewal_Time) {
                Renewal_Time = -1;
                this.createMessage(clock, this.getAgentID(), getInfra().nprocess, PG_PRESENT, new Content_PGC(clock, this.getAgentID()), -1);
                Renewal_Time = clock + pi;
            }
            if (clock == CRT) {
                getInfra().debug("p"+getAgentID()+": sinc, clock="+clock);
                Content_Sync sc = new Content_Sync(clock + ro * tick);
                lastClock++;
                this.createMessage(clock,this.getAgentID(),getInfra().nprocess,CK_REQ,sc,lastClock);
                somaClocks = 0.0;
                numeroClocks = 0;
                CRT = clock + Clock_Renewal_Time;
            }
            
        }

        public void deliver(Message msg) {
            if (msg.type == PG_APP)
                   this.preDeliver(msg);
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
            long V;
            int M;
            int clock = (int)getInfra().clock.value();
            int tick  = (int)getInfra().clock.tickValue();
            double DESVIO = getInfra().context.get(RuntimeSupport.Variable.MaxDeviation).<Double>value();
            double ro = getInfra().context.get(RuntimeSupport.Variable.ClockDeviation).<Double>value();
            Network network = getInfra().context.get(RuntimeSupport.Variable.Network).<Network>value();
            Content_Sync sc;
            switch (msg.type) {
                case PG_NEW_GROUP: 
                    V = ( (Content_PGC) msg.content).V;
                    M = ( (Content_PGC) msg.content).M;
                    // if (V <= clock) {
                        this.createMessage(V, this.getAgentID(), getInfra().nprocess, PG_PRESENT, new Content_PGC(V, getAgentID()), 0 );
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
                    long t_e = msg.physicalClock + (int) DESVIO + (1+resiliencia) * DELTA;
                    if (msgRecebidas.contains(msg.getId()) == false) {
                        getInfra().debug("p"+getAgentID()+": relayed from "+msg.sender);
                        msgRecebidas.add(msg.getId());
                        if (msg.hops < resiliencia) {
                            msg.hops++;
                            for (int i=0; i< getInfra().nprocess; i++) {
                                if (  (i != getAgentID()) && (i != msg.relayFrom) )
                                relayMessage(clock, msg, i);
                            }
                        };
                        getInfra().app_in.add(t_e, msg);
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
                    this.createMessage(clock,this.getAgentID(),msg.sender,CK_REP,sc, msg.logicalClock);
                    break;
                case CK_REP:
                    double agora = clock + ro * tick;
                    sc = (Content_Sync) msg.content;
                    if (msg.logicalClock == lastClock) {
                        //System.nic_out.println("CLK");
                        numeroClocks++;
                        somaClocks += sc.atual+(agora-sc.inicio)/2;
                        if (numeroClocks == getInfra().nprocess) {
                            getInfra().debug("p"+getAgentID()+" clock atual:"+clock+" tick:"+tick);
                            double ajusta = somaClocks/numeroClocks;
                            double atual = clock + ro * tick;
                            somaClocks = .0;
                            numeroClocks=0;
                            if (ajusta < atual){
                                getInfra().clock.adjustCorrection((int) ( (atual - ajusta)*(1/ro) ));
                            }
                            else {
                                getInfra().clock.adjustValue((int)ajusta);
                                getInfra().clock.adjustTickValue((int) (((ajusta - (int) ajusta)) * (1/ro)));
                            }
                        }
                    }
                    break;
                    
            }
            
        }
        
}
