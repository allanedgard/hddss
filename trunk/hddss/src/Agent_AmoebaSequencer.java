public class Agent_AmoebaSequencer extends SimulatedAgent {
    
    
    /*
     * O detector de defeitos  está incorporado ao timeout do TimedCB
     * contudo o detector de estados não
     * o mesmo deve ser implementado de modo a monitorar os canais
     * e decidir o estado
     */
    
        int DELTA;
        int delta;
        int ts;
        double prob;
        int LogicalClock;
        int LastTimeSent;
        final int CHANGE_VIEW_REQUEST = 6;
        final int UNSTABLE = 7;
        final int CONSENSUS_P1 = 8;
        final int CONSENSUS_P2 = 9;
        final int DECIDED = 10;
        final int REQ_SEQ = 11;
        final int APP = 12;
        final int ACK = 13;
        final int DLV = 14;
        int RECV;
        int SENT;
        
        // 
        int Lider;
        boolean SouLider;
        int LSN; 
        
        int StableMode;
        
        int minblocoexpirado = Integer.MAX_VALUE;
        int [] BM;
        int [] LCB;
   //     char [] [] BM_Matrix;
        Consensus [] Consensus;
        int [] agendador;
        int maiorBloco;
        
        Buffer msgs;
        java.util.TreeMap blocos;
        java.util.ArrayList bufferDeMensagens;
        java.util.ArrayList bufferDeMensagensInstaveis;
        java.util.ArrayList blocosexpirados;
        java.util.ArrayList UnstableMensagensEnviadas;
        java.util.ArrayList AllUnstableMensagens;
        java.util.TreeMap agenda;
        IntegerSet visaoProposta;
                
        IntegerSet down;
        IntegerSet live;
        IntegerSet uncertain;
        IntegerSet suspected;
        IntegerSet visao;
                
        boolean bloquearEntrega;
        boolean consenso;
        int Sequencia;
            
        Randomize r;
        
        int [] timeout;
        int [] quorum;        
        
        Agent_AmoebaSequencer() {
        super();
        }
    
    @Override
        public void setup() {
            LSN = -1;
            bloquearEntrega = false;
            consenso = false;
            Lider = 1;
            if (id==Lider) 
                SouLider = true;
            r = new Randomize();
            /*  Constrói os blocos básicos:
             *  - Buffer de mensagens recebidas
             *  - blocos de mensagens
             *  - lista de blocos que foram expirados
             *  - mensagens instáveis
             */
            bufferDeMensagens = new java.util.ArrayList();
            msgs = new Buffer();
            
            Sequencia = -1;
            SENT = Integer.MAX_VALUE;

            
            
            /* Flags para bloquear a entrega 
             * e startup o consenso
             */ 
            bloquearEntrega = false;
            consenso = false;
            
            /* Conjuntos a serem mantidos pelos 
             * Detectores de Estados e de Defeitos
             */
            down = new IntegerSet();
            live = new IntegerSet();
            uncertain = new IntegerSet();
            suspected = new IntegerSet();
            visao = new IntegerSet();

            
            visaoProposta = new IntegerSet();
            
            iniciaConjuntos();
            quorum= new int[100000];
            timeout= new int[100000];
            // Mensagens instáveis que serão usadas no consenso
            AllUnstableMensagens =  new java.util.ArrayList();
            
            // Consensos que podem ser mantidos por bloco
            Consensus = new Consensus[infra.context.tempofinal*2];
            
            
        }
        
        public void iniciaConjuntos() {
            /*
             *  Inicia conjuntos, incluindo todos os processos 
             *  como LIVE na visao!
             */ 
            for (int i=0;i<infra.nprocess;i++) {
                live.adiciona(i);
                visao.adiciona(i);
            }
        }

        /*
         *  Definem parâmetros do algoritmo
         */
        
        public void setDeltaMaximo(String dt) {
            DELTA = Integer.parseInt(dt);
        }

        public void setProbabilidadeGerarPacote (String po) {
            prob = Double.parseDouble(po);
        }
 
        
    @Override
        public void startup(){

        }   
     
        
    @Override
        public void execute() {
            if ( (r.uniform() <= prob) && !bloquearEntrega ) {
                int clock = (int)infra.clock.value();
                SENT = clock;    // Registra numero do bloco do ultimo envio
                this.criamensagem(clock, id, Lider, REQ_SEQ, "payload", -1);
            }
            /*
            if ((clock-SENT) > DELTA) {
                enviaMensagemGrupo(clock, CHANGE_VIEW_REQUEST, Sequencia, LogicalClock);
                down.adiciona(Lider);
                live.remove(Lider);
                suspected.limpa();
                //suspected.adiciona();
            }*/
            //alterarAgenda(clock-1, -1);
        }
        
        /*
         *  Envia mensagem a grupo
         */
        public void enviaMensagemGrupo(int clock, int tipo, Object valor, int LC) {
            for (int j=0; j<infra.nprocess;j++)
                this.criamensagem(clock, this.id, j, tipo, valor, LC);
        }
        
        public void enviaMensagemGrupo(int clock, int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<infra.nprocess;j++)
                this.criamensagem(clock, this.id, j, tipo, valor, LC, pay);
        }

        public void relayMensagemGrupo(int clock, int i, int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<infra.nprocess;j++)
                this.criamensagem(clock, i, j, tipo, valor, LC, pay);
        }        

        
        /* 
         *   Este evento pode ser sobrecarregado pela ação específica 
         *   do protocolo, execute a recepção de mensagens
         */
    @Override
        public void receive(Message msg) {
            Content_Unstable uC;
            Consensus c;
            int clock = (int)infra.clock.value();
            switch (msg.tipo) {
                case REQ_SEQ:
                    if (SouLider) {
                        Sequencia++;
                        relayMensagemGrupo(clock, msg.remetente, APP, msg.conteudo, Sequencia, true);
                        quorum[Sequencia]=0;
                    }
                    break;
                case APP:
                    msgs.adiciona(msg.relogioLogico, msg);
                    this.criamensagem(clock, id, Lider, ACK, msg, msg.relogioLogico);      
                    
                    break;
                case ACK:
                    if (SouLider) {
                        quorum[msg.relogioLogico]++;
                        infra.debug("seq "+Sequencia+" count "+quorum[Sequencia]);
                        if ( quorum[msg.relogioLogico] == infra.nprocess )
                            this.enviaMensagemGrupo(clock, DLV, msg.conteudo, msg.relogioLogico);                   
                    }
                    break;
                case DLV:
                    infra.app_in.adiciona(clock, (Message) msg.conteudo);
                    break;
                case CHANGE_VIEW_REQUEST:
                    if ( UnstableMensagensEnviadas.contains(msg.conteudo) )
                            break;
                    UnstableMensagensEnviadas.add(msg.conteudo);
                    bloquearEntrega = true;
                    int b = Integer.parseInt((String) msg.conteudo);
                    uC = new Content_Unstable(b, bufferDeMensagensInstaveis, msg.remetente, down);
                    enviaMensagemGrupo(clock, UNSTABLE, uC, LogicalClock);
                    break;
                case UNSTABLE:
                    if (!consenso) {
                        uC = (Content_Unstable) msg.conteudo;
                        // DOWN = DOWN U DOWN PERCEBIDO!
                        down.adiciona(uC.down); 
                        adicionaVisao(uC);
                        if (obtidoUnstableMensagens() ) {
                            consenso = true;
                            down.adiciona(live);
                            live.adiciona(down);
                            down.remove(visaoProposta);
                            uC.down.limpa();
                            uC.down.adiciona(down);
                            uC.visaoProposta.limpa();
                            uC.visaoProposta.adiciona(this.visaoProposta);

                            Consensus[uC.bloco] = iniciaConsensus(uC);
                            
                            if (Consensus[uC.bloco].getRound()%infra.nprocess == id)
                                enviaMensagemGrupo(clock, CONSENSUS_P1,Consensus[uC.bloco], LogicalClock);
                        
                        }
                    }
                    break;
                case CONSENSUS_P1:
                    c = (Consensus) msg.conteudo;
                    // Mensagem do mesmo round
                    if (Consensus[c.numero]==null)
                        Consensus[c.numero] = iniciaConsensus( (Content_Unstable) c.estimado );
                    
                    if (c.getRound()==(Consensus[c.numero].getRound())) {
                        if (msg.remetente == c.getRound() % infra.nprocess) {
                            Consensus[c.numero].estimado = c.estimado;
                            enviaMensagemGrupo(clock, CONSENSUS_P2,Consensus[c.numero], LogicalClock);
                        }
                    }
                    else if  (c.getRound()<(Consensus[c.numero].getRound())){
                                c.alteraRound(Consensus[c.numero].getRound() );
                                if (msg.remetente == c.getRound() % infra.nprocess) {
                                   Consensus[c.numero].estimado = c.estimado;
                                    enviaMensagemGrupo(clock, CONSENSUS_P2,Consensus[c.numero], LogicalClock);
                                }
                    }                    
                    break;
                case CONSENSUS_P2:
                    c = (Consensus) msg.conteudo;
                    /* 
                     *  Se recebe a mensagem de FASE 2 do consenso n:
                     *  - verifica se há um QUÓRUM de processos:
                     *      - se não há inclui as contribuições no
                     *        conjunto de mensagens recebidas
                     */
                    if (Consensus[c.numero]==null)
                        Consensus[c.numero] = iniciaConsensus( (Content_Unstable) c.estimado );
                    
                    if (!Consensus[c.numero].atingiuQuorum) {
                        Consensus[c.numero].quorum.adiciona(msg.remetente);
                        // adicionaREC(c.numero, (UnstableContent) c.estimado);
                        if ( ((Content_Unstable) c.estimado).tamanho() != 0)
                                Consensus[c.numero].rec = c.estimado;
                        if (obtidoQuorumConsenso(c.numero)) {
                            if ( ((Content_Unstable) c.estimado).tamanho() == 0)
                                Consensus[c.numero].noneREC = true;               
                            if ( ( (Content_Unstable) Consensus[c.numero].rec).conteudo.size() == 0)
                            {
                                if (Consensus[c.numero].noneREC) {
                                /*      - se sim, se a decisão é somente {_|_} 
                                */
                                rotacionaCoordenador(c.numero);
                                }
                                else {
                                    /*      - se sim, se a decisão é {v, _|_} 
                                    */
                                    rotacionaCoordenador(c.numero);
                                    Consensus[c.numero].estimado = Consensus[c.numero].rec;
                                }
                            } 
                            else {
                                /*      - se sim, se a decisão é {v} 
                                */                        
                                enviaMensagemGrupo(clock, DECIDED,Consensus[c.numero], LogicalClock);
                            }
                        } 
                    }

                    break;               
                
                case DECIDED:
                    /*
                     *  Se recebe a mensagem do consenso n estar decidido e 
                     *  as mensagens ainda não haviam sido entregues:
                     *  - entrega as mensagens;
                     *  - finaliza o consenso n.
                     */
                    c = (Consensus) msg.conteudo;
                    // Implementa a decisao
                    if (Consensus[c.numero].ativo) {
                        Consensus[c.numero].ativo = false;
                        Consensus[c.numero].estimado = c.rec;
                        uC  =  (Content_Unstable) Consensus[c.numero].estimado;
                        java.util.ArrayList x = (java.util.ArrayList) uC.conteudo;
                        for (int i=0;i<x.size();i++) {
                            Message m = (Message) x.get(i);
                            if (m.relogioLogico > BM[m.remetente])
                                BM[m.remetente] = m.relogioLogico;
                            bufferDeMensagens.add(m);
                        }
                        bloquearEntrega = false;
                        down.adiciona(live);
                        live = live.interseccao(uC.visaoProposta);
                        uncertain = uncertain.interseccao(uC.visaoProposta);
                        down.remove(uC.visaoProposta);
                        suspected.limpa();
                        visao.limpa();
                        visao.adiciona(visaoProposta);
                        System.out.println("Consenso Obtido em "+clock);
                        System.out.println("Visao em p"+id);
                        for (int i=0; i<infra.nprocess; i++)
                            if (visao.existe(i) )
                                System.out.print(i+" \t");
                        System.out.println("");
                        Lider = visao.minimo();
                        if (id == Lider) {
                            SouLider = true;
                        }
                        else {
                            SouLider = false;
                        }
                            
                    }
            }
            
        }
        
        /*
         *  Altera o coordenador do consenso
         */
        void rotacionaCoordenador(int i) {
            Consensus[i].alteraRound(Consensus[i].getRound()+1);
            int clock = (int)infra.clock.value();
            if (Consensus[i].getRound()%infra.nprocess == id)
                enviaMensagemGrupo(clock, CONSENSUS_P1,Consensus[i], LogicalClock);            
        }
        
        
        /*
         *  Cria um novo consenso
         */
        Consensus iniciaConsensus(Content_Unstable uC) {
            Consensus c = new Consensus(uC.bloco,0,uC);
            return c;
        }

        int maximo(int a, int b) {
            if (a>b)
                return a;
            else
                return b;
        }
        
        /* 
         *  Cria uma nova visão com os processos que participaram
         */
        void adicionaVisao(Content_Unstable uC) {
            if (!visaoProposta.existe(uC.id)) {
                visaoProposta.adiciona(uC.id);
                infra.debug(visaoProposta.toString());
                for (int i = 0; i < uC.conteudo.size(); i++ )
                    if (!AllUnstableMensagens.contains(uC.conteudo.get(i)))
                        AllUnstableMensagens.add(uC.conteudo.get(i));
            }
        }
        
        /*
         *   Adiciona aos valores recebidos na construção do consenso
         */
        void adicionaREC(int numero, Content_Unstable uC) {
            for (int i = 0; i < uC.conteudo.size(); i++ )
                    if (! ( ((java.util.ArrayList) Consensus[numero].rec).contains(uC.conteudo.get(i))  )  )
                        ((java.util.ArrayList) Consensus[numero].rec).add(uC.conteudo.get(i));
        }
        
        /*
         *   Verifica se obteve todas as UnstableMensagens
         */
        boolean obtidoUnstableMensagens() {
            boolean liveOk;
            boolean uncertainOk;
            
            liveOk = (live.tamanho()==live.interseccao(visaoProposta).tamanho());
            
            infra.debug("live ok? "+liveOk);
            infra.debug("tam live="+live.tamanho());
            infra.debug("tam live inter View="+live.interseccao(visaoProposta).tamanho());
            infra.debug("live = "+live);
            infra.debug("view = "+visaoProposta);
            
            int contaUncertain=visaoProposta.interseccao(uncertain).tamanho();

            if (uncertain.tamanho() >0) {
                float perc = contaUncertain / uncertain.tamanho();  
                if (perc>.5) {uncertainOk = true;} else {uncertainOk = false;};
            } else 
                uncertainOk = true;
            
            boolean ret = (liveOk&&uncertainOk);
            infra.debug("ok? "+ ret);
            infra.debug("end check");
            return (liveOk && uncertainOk);
        }

        /*
         *   Verifica se obteve todas as UnstableMensagens
         */
        boolean obtidoQuorumConsenso(int i) {
            boolean liveOk;
            boolean uncertainOk;
            
            liveOk = (live.tamanho()==live.interseccao(Consensus[i].quorum).tamanho());
            
            int contaUncertain=Consensus[i].quorum.interseccao(uncertain).tamanho();

            if (uncertain.tamanho() >0) {
                float perc = contaUncertain / uncertain.tamanho();  
                if (perc>.5) {uncertainOk = true;} else {uncertainOk = false;};
            } else 
                uncertainOk = true;

            return (liveOk && uncertainOk);
        }
        
}