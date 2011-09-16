package br.ufba.lasid.jds.prototyping.hddss;

import java.util.ArrayList;

public class Agent_AmoebaSequencer extends SimulatedAgent {
    
    
    /*
     * O detector de defeitos  está incorporado ao timeout do TimedCB
     * contudo o detector de estados não
     * o mesmo deve ser implementado de modo a monitorar os canais
     * e decidir o estado
     */
        int LastClock;
        int LastACK;
        int LastDLV;
        int LastAccepted;
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
            int finalTime = infra.context.get(RuntimeSupport.Variable.FinalTime).<Integer>value();
            LSN = -1;
            bloquearEntrega = false;
            consenso = false;
            Lider = 1;
            if (ID==Lider)
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
            LastDLV = -1;
            LastAccepted = -1;
            
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
            BM = new int[infra.nprocess];
            for (int i=0;i<infra.nprocess;i++)
                BM[i]=-1;
            
            visaoProposta = new IntegerSet();
            
            initializeSets();
            quorum= new int[1000000];
            timeout= new int[1000000];
            // Mensagens instáveis que serão usadas no consenso
            AllUnstableMensagens =  new java.util.ArrayList();
            
            // Consensos que podem ser mantidos por bloco
            Consensus = new Consensus[finalTime*2];
            
            
        }
        
        public void initializeSets() {
            /*
             *  Inicia conjuntos, incluindo todos os processos 
             *  como LIVE na visao!
             */ 
            for (int i=0;i<infra.nprocess;i++) {
                live.add(i);
                visao.add(i);
            }
        }

        /*
         *  Definem parâmetros do algoritmo
         */
        
        public void setDeltaMax(String dt) {
            DELTA = Integer.parseInt(dt);
        }

        public void setTS(String dt) {
            ts = Integer.parseInt(dt);
        } 
        
        public int getDelta() {
            return DELTA;
        }
        
        public void setPacketGenerationProb (String po) {
            prob = Double.parseDouble(po);
        }
 
        
    @Override
        public void startup(){

        }   
     
        
    @Override
        public void execute() {
            Content_Amoeba ca = new Content_Amoeba(LastACK, "stuff");
            if ( (r.uniform() <= prob) && !bloquearEntrega ) {
                int clock = (int)infra.clock.value();
                SENT = clock;    // Registra numero do bloco do ultimo envio
                this.createMessage(clock, ID, Lider, REQ_SEQ, ca, -1);
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
        public void sendGroupMsg(int clock, int tipo, Object valor, int LC) {
            for (int j=0; j<infra.nprocess;j++)
                this.createMessage(clock, this.ID, j, tipo, valor, LC);
        }
        
        public void sendGroupMsg(int clock, int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<infra.nprocess;j++)
                this.createMessage(clock, this.ID, j, tipo, valor, LC, pay);
        }

        public void relayGroupMsg(int clock, int i, int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<infra.nprocess;j++)
                this.createMessage(clock, i, j, tipo, valor, LC, pay);
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
            switch (msg.type) {
                case REQ_SEQ:
                    if (SouLider) {
                        Sequencia++;
                        Content_Amoeba_Reply cm = new Content_Amoeba_Reply(LastDLV, msg);
                        relayGroupMsg(clock, msg.sender, APP, cm, Sequencia, true);
                        quorum[Sequencia]=0;
                        /*
                         *   PROCESSA OS EMBEDDED ACKS 
                         */
                        if (BM[msg.sender] < ((Content_Amoeba)msg.content).getLast()) {
                            for (int i=BM[msg.sender]+1;i<=((Content_Amoeba)msg.content).getLast();i++){
                                quorum[i]++;
                                infra.debug("seq "+i+" count "+quorum[i]);
                                if ( quorum[i] == infra.nprocess ) {
                                    if (clock - SENT >= ts)
                                        this.sendGroupMsg(clock, DLV, msg, msg.logicalClock);
                                    LastDLV = msg.logicalClock; }
                                }
                            BM[msg.sender] = ((Content_Amoeba)msg.content).getLast();
                        }
                    }
                    break;
                case APP:
                    msgs.add(msg.logicalClock, ((Content_Amoeba_Reply)msg.content).getContent() );
                    int lastAcc = ((Content_Amoeba_Reply)msg.content).getAccept();
                    for (int i=LastAccepted+1;i<=lastAcc;i++){
                        if (msgs.checkTime(i)) {
                            ArrayList a =  (msgs.getMsgs(i));
                            Message me =  (Message) a.get(0);
                            infra.app_in.add(clock, me );
                            Simulator.reporter.stats("blocking time", clock - me.receptionTime);
                        }
                    };
                    LastAccepted = lastAcc;
                    /* 
                     * SÓ ENVIAR ACK SE PASSOU SILENCE
                     */
                    if (clock - SENT >= ts)
                        this.createMessage(clock, ID, Lider, ACK, msg, msg.logicalClock);
                    LastACK = msg.logicalClock;
                    ((Content_Amoeba_Reply) msg.content).getAccept();                    
                    break;
                case ACK:
                    /*
                    if (SouLider) {
                        quorum[msg.logicalClock]++;
                        infra.debug("seq "+Sequencia+" count "+quorum[Sequencia]);
                        if ( quorum[msg.logicalClock] == infra.nprocess )
                            this.sendGroupMsg(clock, DLV, msg.content, msg.logicalClock);
                    }
                    */
                    if (SouLider) {
                        if (BM[msg.sender] < msg.logicalClock) {
                            for (int i=BM[msg.sender]+1;i<=msg.logicalClock;i++){
                                quorum[i]++;
                                infra.debug("seq "+i+" count "+quorum[i]);
                                if ( quorum[i] == infra.nprocess )
                                    if (clock - SENT >= ts)
                                        this.sendGroupMsg(clock, DLV, msg, msg.logicalClock);
                                    LastDLV = msg.logicalClock;
                                }
                            BM[msg.sender] = msg.logicalClock;
                        }
                    }
                    break;
                case DLV:
                    infra.app_in.add(clock, (Message) msg.content);
                    Simulator.reporter.stats("blocking time", clock - ((Message) msg.content).receptionTime);
                    break;
                case CHANGE_VIEW_REQUEST:
                    if ( UnstableMensagensEnviadas.contains(msg.content) )
                            break;
                    UnstableMensagensEnviadas.add(msg.content);
                    bloquearEntrega = true;
                    int b = Integer.parseInt((String) msg.content);
                    uC = new Content_Unstable(b, bufferDeMensagensInstaveis, msg.sender, down);
                    sendGroupMsg(clock, UNSTABLE, uC, LogicalClock);
                    break;
                case UNSTABLE:
                    if (!consenso) {
                        uC = (Content_Unstable) msg.content;
                        // DOWN = DOWN U DOWN PERCEBIDO!
                        down.add(uC.down);
                        adicionaVisao(uC);
                        if (obtidoUnstableMensagens() ) {
                            consenso = true;
                            down.add(live);
                            live.add(down);
                            down.remove(visaoProposta);
                            uC.down.clean();
                            uC.down.add(down);
                            uC.visaoProposta.clean();
                            uC.visaoProposta.add(this.visaoProposta);

                            Consensus[uC.bloco] = iniciaConsensus(uC);
                            
                            if (Consensus[uC.bloco].getRound()%infra.nprocess == ID)
                                sendGroupMsg(clock, CONSENSUS_P1,Consensus[uC.bloco], LogicalClock);
                        
                        }
                    }
                    break;
                case CONSENSUS_P1:
                    c = (Consensus) msg.content;
                    // Mensagem do mesmo round
                    if (Consensus[c.number]==null)
                        Consensus[c.number] = iniciaConsensus( (Content_Unstable) c.estimated );
                    
                    if (c.getRound()==(Consensus[c.number].getRound())) {
                        if (msg.sender == c.getRound() % infra.nprocess) {
                            Consensus[c.number].estimated = c.estimated;
                            sendGroupMsg(clock, CONSENSUS_P2,Consensus[c.number], LogicalClock);
                        }
                    }
                    else if  (c.getRound()<(Consensus[c.number].getRound())){
                                c.alteraRound(Consensus[c.number].getRound() );
                                if (msg.sender == c.getRound() % infra.nprocess) {
                                   Consensus[c.number].estimated = c.estimated;
                                    sendGroupMsg(clock, CONSENSUS_P2,Consensus[c.number], LogicalClock);
                                }
                    }                    
                    break;
                case CONSENSUS_P2:
                    c = (Consensus) msg.content;
                    /* 
                     *  Se recebe a mensagem de FASE 2 do consenso n:
                     *  - verifica se há um QUÓRUM de processos:
                     *      - se não há inclui as contribuições no
                     *        conjunto de mensagens recebidas
                     */
                    if (Consensus[c.number]==null)
                        Consensus[c.number] = iniciaConsensus( (Content_Unstable) c.estimated );
                    
                    if (!Consensus[c.number].gotQuorum) {
                        Consensus[c.number].quorum.add(msg.sender);
                        // adicionaREC(c.numero, (UnstableContent) c.estimado);
                        if ( ((Content_Unstable) c.estimated).tamanho() != 0)
                                Consensus[c.number].rec = c.estimated;
                        if (obtidoQuorumConsenso(c.number)) {
                            if ( ((Content_Unstable) c.estimated).tamanho() == 0)
                                Consensus[c.number].noneREC = true;
                            if ( ( (Content_Unstable) Consensus[c.number].rec).conteudo.size() == 0)
                            {
                                if (Consensus[c.number].noneREC) {
                                /*      - se sim, se a decisão é somente {_|_} 
                                */
                                rotacionaCoordenador(c.number);
                                }
                                else {
                                    /*      - se sim, se a decisão é {v, _|_} 
                                    */
                                    rotacionaCoordenador(c.number);
                                    Consensus[c.number].estimated = Consensus[c.number].rec;
                                }
                            } 
                            else {
                                /*      - se sim, se a decisão é {v} 
                                */                        
                                sendGroupMsg(clock, DECIDED,Consensus[c.number], LogicalClock);
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
                    c = (Consensus) msg.content;
                    // Implementa a decisao
                    if (Consensus[c.number].active) {
                        Consensus[c.number].active = false;
                        Consensus[c.number].estimated = c.rec;
                        uC  =  (Content_Unstable) Consensus[c.number].estimated;
                        java.util.ArrayList x = (java.util.ArrayList) uC.conteudo;
                        for (int i=0;i<x.size();i++) {
                            Message m = (Message) x.get(i);
                            if (m.logicalClock > BM[m.sender])
                                BM[m.sender] = m.logicalClock;
                            bufferDeMensagens.add(m);
                        }
                        bloquearEntrega = false;
                        down.add(live);
                        live = live.intersection(uC.visaoProposta);
                        uncertain = uncertain.intersection(uC.visaoProposta);
                        down.remove(uC.visaoProposta);
                        suspected.clean();
                        visao.clean();
                        visao.add(visaoProposta);
                        System.out.println("Consenso Obtido em "+clock);
                        System.out.println("Visao em p"+ID);
                        for (int i=0; i<infra.nprocess; i++)
                            if (visao.exists(i) )
                                System.out.print(i+" \t");
                        System.out.println("");
                        Lider = visao.min();
                        if (ID == Lider) {
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
            if (Consensus[i].getRound()%infra.nprocess == ID)
                sendGroupMsg(clock, CONSENSUS_P1,Consensus[i], LogicalClock);
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
            if (!visaoProposta.exists(uC.id)) {
                visaoProposta.add(uC.id);
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
            
            liveOk = (live.size()==live.intersection(visaoProposta).size());
            
            infra.debug("live ok? "+liveOk);
            infra.debug("tam live="+live.size());
            infra.debug("tam live inter View="+live.intersection(visaoProposta).size());
            infra.debug("live = "+live);
            infra.debug("view = "+visaoProposta);
            
            int contaUncertain=visaoProposta.intersection(uncertain).size();

            if (uncertain.size() >0) {
                float perc = contaUncertain / uncertain.size();
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
            
            liveOk = (live.size()==live.intersection(Consensus[i].quorum).size());
            
            int contaUncertain=Consensus[i].quorum.intersection(uncertain).size();

            if (uncertain.size() >0) {
                float perc = contaUncertain / uncertain.size();
                if (perc>.5) {uncertainOk = true;} else {uncertainOk = false;};
            } else 
                uncertainOk = true;

            return (liveOk && uncertainOk);
        }
        
}