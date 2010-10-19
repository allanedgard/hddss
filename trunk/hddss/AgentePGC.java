/*
 * To change this template, choose Tools | Templates
 * and open the template nic_in the editor.
 */

/*
 *
 * PREVER ATRASO NOS ticks 
 *
 *
 */


/**
 *
 * @author allan
 */
public class AgentePGC extends SimulatedAgent {
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
        Randomico r;
        java.util.ArrayList msgRecebidas;
        
        AgentePGC() {
        super();

        }

        public void setup() {
            membership = new int[infra.nprocess];
            Renewal_Time = -1;
            r = new Randomico(id);
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
            this.criamensagem(clock, this.id, infra.nprocess, PG_NEW_GROUP, new PG_Conteudo(clock+DELTA, id), -1 );
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
            infra.debug("p"+id+": clock="+clock);
            if (r.uniform() <= prob) {
                LogicalClock ++;
                this.criamensagem(clock, this.id, infra.nprocess, PG_APP, "payload", LogicalClock, true );
            }
            if (clock == Renewal_Time) {
                Renewal_Time = -1;
                this.criamensagem(clock, this.id, infra.nprocess, PG_PRESENT, new PG_Conteudo(clock, id), -1);
                Renewal_Time = clock + pi;
            }
            if (clock == CRT) {
                infra.debug("p"+id+": sinc, clock="+clock);
                Sinc_Conteudo sc = new Sinc_Conteudo(clock + infra.context.ro * tick);
                lastClock++;
                this.criamensagem(clock,this.id,infra.nprocess,CK_REQ,sc,lastClock);
                somaClocks = 0.0;
                numeroClocks = 0;
                CRT = clock + Clock_Renewal_Time;
            }
            
        }

        public void deliver(Mensagem msg) {
            if (msg.relogioLogico > LogicalClock) {
                LogicalClock = msg.relogioLogico+1;
            }
            else {
                LogicalClock ++;
            }
        }
        
        public void receive(Mensagem msg) {
            /* 
            *   Este evento pode ser sobrecarregado pela ação específica 
            *   do protocolo
            */
            int V;
            int M;
            int clock = (int)infra.clock.value();
            int tick  = (int)infra.clock.tickValue();

            Sinc_Conteudo sc;
            switch (msg.tipo) {
                case PG_NEW_GROUP: 
                    V = ( (PG_Conteudo) msg.conteudo).V;
                    M = ( (PG_Conteudo) msg.conteudo).M;
                    // if (V <= clock) {
                        this.criamensagem(V, this.id, infra.nprocess, PG_PRESENT, new PG_Conteudo(V, id), 0 );
                        Renewal_Time = V+pi;
                    // }                    
                    break;
                case PG_PRESENT:
                    V = ( (PG_Conteudo) msg.conteudo).V;
                    M = ( (PG_Conteudo) msg.conteudo).M;
                    membership[M] = V; 
                    break;
                case PG_APP:
                    /* Alterado para incluir Flood
                     * 
                     */
                    int t_e = msg.relogioFisico + (int) infra.context.DESVIO + (1+resiliencia) * DELTA;
                    if (msgRecebidas.contains(msg.getId()) == false) {
                        infra.debug("p"+id+": relayed from "+msg.remetente);
                        msgRecebidas.add(msg.getId());
                        if (msg.hops < resiliencia) {
                            msg.hops++;
                            for (int i=0; i< infra.nprocess; i++) {
                                if (  (i != id) && (i != msg.relayFrom) )
                                relaymensagem(clock, msg, i);
                            }
                        };
                        infra.app_in.adiciona(t_e, msg);
                    } else {
                        infra.context.network.unicasts[msg.tipo]++;
                    }                        
                    // (int r, int d, int t, int rL, int rF, Object c);
                    // Original  int t_e = msg.relogioFisico + (int) controle.DESVIO + DELTA;
                    // Entrega.adiciona(t_e, msg);
                    break;
                case CK_REQ:
                    sc = (Sinc_Conteudo) msg.conteudo;
                    sc.atual = clock + infra.context.ro * tick;
                    this.criamensagem(clock,this.id,msg.remetente,CK_REP,sc, msg.relogioLogico);
                    break;
                case CK_REP:
                    double agora = clock + infra.context.ro * tick;
                    sc = (Sinc_Conteudo) msg.conteudo;
                    if (msg.relogioLogico == lastClock) {
                        //System.nic_out.println("CLK");
                        numeroClocks++;
                        somaClocks += sc.atual+(agora-sc.inicio)/2;
                        if (numeroClocks == infra.nprocess) {
                            infra.debug("p"+id+" clock atual:"+clock+" tick:"+tick);
                            double ajusta = somaClocks/numeroClocks;
                            double atual = clock + infra.context.ro * tick;
                            somaClocks = .0;
                            numeroClocks=0;
                            if (ajusta < atual){
                                infra.clock.adjustCorrection((int) ( (atual - ajusta)*(1/infra.context.ro) ));
                            }
                            else {
                                infra.clock.adjustValue((int)ajusta);
                                infra.clock.adjustTickValue((int) (((ajusta - (int) ajusta)) * (1/infra.context.ro)));
                            }
                        }
                    }
                    break;
                    
            }
            
        }
        
}
