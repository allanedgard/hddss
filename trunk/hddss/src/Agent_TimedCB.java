public class Agent_TimedCB extends SimulatedAgent {
    
    
    /*
     * O detector de defeitos  está incorporado ao timeout do TimedCB
     * contudo o detector de estados não
     * o mesmo deve ser implementado de modo a monitorar os canais
     * e decidir o estado
     */
    
        int DELTA;
        int delta;
        int ts;
        int tsmax;
        double prob;
        int LogicalClock;
        int LastTimeSent;
        final int TIMEDCB_APP = 4;
        final int TIMEDCB_TS = 5;
        final int CHANGE_VIEW_REQUEST = 6;
        final int UNSTABLE = 7;
        final int CONSENSUS_P1 = 8;
        final int CONSENSUS_P2 = 9;
        final int DECIDED = 10;
        int RECV;
        int SENT;
        
        int StableMode;
        
        int minblocoexpirado = Integer.MAX_VALUE;
        int [] BM;
        int [] LCB;
   //     char [] [] BM_Matrix;
        Consensus [] Consensus;
        int [] agendador;
        int [] ultimaMsgTimeStamp;
        Acknowledge [] acks;
        int maiorBloco;
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
        
        Randomize r;
        
        Agent_TimedCB() {
        super();
        }
        
        int verAgenda(int t) {
            int B = -1;
            if ( agenda.containsKey(t) )
                    B = (Integer) agenda.get(t);
            return B;
        }
        
        void alterarAgenda(int t, int B) {
            if (B==-1) {
                agenda.remove(t);
            }
            else if (verAgenda(t)<B) {
                agenda.remove(t);
                agenda.put(t, B);
            }
        }
        

    @Override
        public void setup() {
            agenda = new java.util.TreeMap();
            StableMode = 0;
            LogicalClock = 0;
            BM = new int[infra.nprocess];
            LCB = new int[infra.nprocess];
            ultimaMsgTimeStamp = new int[infra.nprocess];

            acks = new Acknowledge[infra.nprocess];
            
            r = new Randomize(id);
            agendador = new int[infra.context.tempofinal*2];
 //           BM_Matrix = new char[controle.n][controle.tempofinal*2];
            for (int j = 0;j<agendador.length ;j++) {
                    agendador[j]=-1;
            }   
            
            /*  Constrói os blocos básicos:
             *  - Buffer de mensagens recebidas
             *  - blocos de mensagens
             *  - lista de blocos que foram expirados
             *  - mensagens instáveis
             */
            bufferDeMensagens = new java.util.ArrayList();
            bufferDeMensagensInstaveis =  new java.util.ArrayList();
            blocos = new java.util.TreeMap();
            blocosexpirados = new java.util.ArrayList();
            UnstableMensagensEnviadas = new java.util.ArrayList();
            
            /*
             *  Ultimo bloco completo e ultimo tempo de envio
             */
            LastTimeSent = -1;  // Último tempo de envio
            maiorBloco = -1;    // Ultimo Bloco Completo
            SENT = -1;
            RECV = -1;
            
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
            
            // Mensagens instáveis que serão usadas no consenso
            AllUnstableMensagens =  new java.util.ArrayList();
            
            // Consensos que podem ser mantidos por bloco
            Consensus = new Consensus[infra.context.tempofinal*2];
            
            // Inicia matriz de blocos e matriz de Last Complete Blocks
            for (int i = 0;i<infra.nprocess;i++) {
                BM[i]=-1;
                LCB[i]=-1;
                acks[i] = new Acknowledge();
            }
            
        }

//        public int estimaTS() {
/*            int ts_atual  = ts/2;
            int alpha  = (1 - 1/40);
            int beta = 1 - alpha;
            double m_p = .1;
            int TC = (int) ((ts +2 *DELTA )* (1+controle.ro*controle.maxro));
            int Ks = TC;
            int errorold = 0;
            int Kp = (int) ( ( alpha - Math.exp(-4/TC) ) / (beta * delta )   );
            int Kd = (int) (  ( Math.exp(-8/TC) + Math.exp(-4/TC)* Math.cos( (4*Math.PI*Math.log(Math.E)) / (TC) )   - (2*alpha + 1)     ) / (beta * delta )   );
            int N = estimaN();

            return ts;*/
//            return ts;
//        }

//        public int estimaN() {
//            return 0;
//        };

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

        public void setStableMode(String dt) {
            StableMode = Integer.parseInt(dt);
        }
        
        public void setDeltaMinimo(String dt) {
            delta = Integer.parseInt(dt);
        }

        public void setTimeSilence(String p) {
            ts = Integer.parseInt(p);
            tsmax = ts;
        }

        public void setProbabilidadeGerarPacote (String po) {
            prob = Double.parseDouble(po);
        }
 
        
        public void startup(){

        }   

       public int getMaxTS(){
           return tsmax;
       }

       public int getDeltaMax(){
           return DELTA;
       }

       public int getDeltaMin(){
           return delta;
       }
       
       void registraBloco(int numbloco, int sender, int ti) {
                int TC;
                Integer bloco = new Integer(numbloco);
                int clock = (int)infra.clock.value();
                if (!blocos.containsKey(bloco)) {
                    /*
                     *  Cria o bloco!
                     */
                        if (bloco > maiorBloco) 
                            maiorBloco = bloco;
                        blocos.put(bloco, clock);
                        if (infra.context.modo== 't') {
                            if (sender == id) {
                                TC = (int) ( (clock+2 * getDeltaMax() + getMaxTS()) + infra.context.DESVIO+1);
                            }
                            else
                                TC = (int) ( (clock+2 * getDeltaMax() + getMaxTS()-getDeltaMin()) + infra.context.DESVIO+1);
                        } else {
                            if (sender == id) {
                                TC = (int) ( (clock+2 *getDeltaMax() + getMaxTS() )* (1+infra.context.ro*infra.context.maxro))+1;
                            }
                            else
                                TC = (int) ( (clock+2 * getDeltaMax() + getMaxTS()-getDeltaMin())* (1+infra.context.ro*infra.context.maxro))+1;
                        }

                        /*  
                         *  Escalona o timeout do bloco!
                         *
                         */
                        
                        if (TC <= infra.context.tempofinal)
                                if (agendador[TC] < numbloco) {
                                    agendador[TC] = numbloco;
                                    infra.debug("p"+id+" bloco "+numbloco+" expira em "+TC+" criado em "+clock+" sender = "+sender);
                                }
                        /*
                                if (verAgenda(TC) < numbloco) {
                                    alterarAgenda(TC,numbloco);
                                    debug("p"+id+" bloco "+numbloco+" expira em "+TC+" criado em "+clock+" sender = "+sender);
                                }*/
                        
                }
        }
       
        
        public void execute() {
            int clock = (int)infra.clock.value();
            if ( (r.uniform() <= prob)  ) {

                LogicalClock ++;        // Ajusta o relógio lógico
                //LastTimeSent = clock;   // Registra clock do ultimo envio
                SENT = LogicalClock;    // Registra numero do bloco do ultimo envio

                enviaMensagemGrupo(clock, TIMEDCB_APP, new Content_TimedCB("payload", LCB[id], acks), LogicalClock, true );
                registraBloco(LogicalClock, id, clock);
            }
            
            /*
             *  Se há silêncio, invoca o mecanismo de time-silence
             *  o mecanismo é invocado mesmo sem blocos completos?
             */
            
            if ( (RECV > SENT) || (bloquearEntrega) ) {
                if (clock - LastTimeSent >= ts) {

                        LastTimeSent = clock;
                        LogicalClock = maximo(LogicalClock+1,maiorBloco);
                        SENT = LogicalClock;

                        enviaMensagemGrupo(clock, TIMEDCB_TS, new Content_TimedCB("time-silent", LCB[id], acks), LogicalClock );
                        registraBloco(LogicalClock, id, clock);
                }
            }
            
            /*
             *  Verifica se há bloco expirado
             *  Se houver envia CHANGE VIEW REQUEST
             *  atualiza down, live e suspected
             *
            if (verAgenda(clock) != -1) {
                 if (obtemMinimo() < verAgenda(clock) ) {
                    for (int i = obtemMinimo()+1; i<=verAgenda(clock) ;i++) {
                        debug("p"+Integer.toString(id)+": timeout nic_in block "+Integer.toString(i) + " nic_in "+Integer.toString(clock));
                        enviaMensagemGrupo(clock, CHANGE_VIEW_REQUEST, Integer.toString(i), LogicalClock);
                        informaBlocoExpirado(i);
                        ConjuntoInteiro x = retornaExpirados(i);

                        down.adiciona(live.interseccao(x));
                        live.remove(x);
                        suspected.limpa();
                        suspected.adiciona(uncertain.interseccao(x));
                    }

                 }
            }*/
            
            if (agendador[clock] != -1) {
                 if (obtemMinimo() < agendador[clock]) {
                    for (int i = obtemMinimo()+1; i<=agendador[clock];i++) {
                        infra.debug("p"+Integer.toString(id)+": timeout in block "+Integer.toString(i) + " in "+Integer.toString(clock));
                        informaBlocoExpirado(i);
                        IntegerSet x = retornaExpirados(i);
                        if (!bloquearEntrega) {
                            enviaMensagemGrupo(clock, CHANGE_VIEW_REQUEST, Integer.toString(i), LogicalClock);
                            bloquearEntrega=true;
                        }
                        down.adiciona(live.interseccao(x));
                        live.remove(x);
                        suspected.limpa();
                        suspected.adiciona(uncertain.interseccao(x));
                    }
                    //if (!bloquearEntrega)
                    //enviaMensagemGrupo(clock, CHANGE_VIEW_REQUEST, Integer.toString(agendador[clock]), LogicalClock);

                 }
            }
            //alterarAgenda(clock-1, -1);
        }
        
        /*
         *  Envia mensagem a grupo
         */
        public void enviaMensagemGrupo(int clock, int tipo, Object valor, int LC) {
            for (int j=0; j<infra.nprocess;j++){
                
                if(valor instanceof Content_TimedCB){
                    ((Content_TimedCB)valor).vack[j].rsendTime = clock;
                }
                this.criamensagem(clock, this.id, j, tipo, valor, LC);
            }
        }
        
        public void enviaMensagemGrupo(int clock, int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<infra.nprocess;j++){
                if(valor instanceof Content_TimedCB){
                    ((Content_TimedCB)valor).vack[j].rsendTime = clock;
                }

                this.criamensagem(clock, this.id, j, tipo, valor, LC, pay);
            }
        }
        
        
        
    /*
     *  Dado um bloco expirou inclui o mesmo na lista de blocos expirados
     */ 
    public void informaBlocoExpirado(int bl) {
            blocosexpirados.add(bl);
            if (minblocoexpirado == Integer.MAX_VALUE) 
                minblocoexpirado = bl;
     }

     /*
     *  Ao efetuar uma entrega atualiza o relogio logico
     */ 
     public void deliver(Message msg) {
            if (msg.relogioLogico > LogicalClock) {
                LogicalClock = msg.relogioLogico+1;
            }
     }

     /*
      *  Indica qual o ultimo bloco completo
      */ 
     public int obtemMinimo() {
        int minimo = Integer.MAX_VALUE;
        for (int i=0; i<infra.nprocess;i++){
            if (visao.existe(i)) {
                if (BM[i] < minimo)   
                    minimo = BM[i]; }
            // else System.nic_out.println("O processo "+i+" não existe na visao de "+id);
        }
        return minimo;
     }
     
     
     /*
     public int obtemMaximo() {
        int maximo = Integer.MIN_VALUE;
        for (int i=0; i<controle.n;i++){
            if (visao.existe(i))
                if (BM[i] > maximo)
                    maximo = BM[i];
        }
        return maximo;
     }
      */
     
     public int obtemMinimoEstavel() {
        int minimo = Integer.MAX_VALUE;
        for (int i=0; i<infra.nprocess;i++){
            if (visao.existe(i))
                if (LCB[i] < minimo)
                    minimo = LCB[i];
        }
        return minimo;
     }
     
     /*
      *  Ajusta a matriz de blocos com o resultado do consenso
      */
     public void informaBlocoPosConsenso(int x) {
        for (int i=0; i<infra.nprocess;i++){
            if (visao.existe(i))
                if (BM[i] < x)
                    BM[i]=x;
        }
     }
     
     /*
      *  Informa quais os processos responsáveis pela expiração do bloco
      */
     public IntegerSet retornaExpirados(int bl) {
        IntegerSet x = new IntegerSet();
        for (int i=0; i<infra.nprocess;i++){
            if (visao.existe(i))
                if (BM[i] < bl)   
                    x.adiciona(i);
        }
        return x;
     }     

     /*
      *  Veficica se blocos estão completos e entrega!
      *  Atualiza o LCB
      *  Atualiza as mensagens instáveis
      */
     void verificaCompletudeCausalBlock() {
        int n = infra.nprocess;
        int minimo = obtemMinimo();
        LCB[id] = minimo;
        int clock = (int)infra.clock.value();
        int minimoEstavel = obtemMinimoEstavel();
        java.util.TreeSet chave = new java.util.TreeSet();
        java.util.TreeMap ordenar = new java.util.TreeMap();
        int ordem;
        Integer od;
        Message m;
        if (bloquearEntrega) return;
        
        if (StableMode == 1)
            minimo = minimoEstavel;

        java.util.Collections.sort(bufferDeMensagens, new java.util.Comparator() {
             public int compare(Object o1, Object o2) {
                 Message msg1 = (Message) o1;
                 Message msg2 = (Message) o2;
                 return     (msg1.relogioLogico - msg2.relogioLogico) != 0 ?
                            (msg1.relogioLogico - msg2.relogioLogico) :
                            (msg1.remetente - msg2.remetente);

             }
        });
        
        //if (LogicalClock < minimo) LogicalClock = minimo;
        int cont=0;
        for (int i=0; i<bufferDeMensagens.size();i++) {
                m = (Message) bufferDeMensagens.get(i);
                // System.nic_out.println("p"+id+" min="+minimo+" ts log = "+m.relogioLogico);
                if ( (m.relogioLogico <= minimo)  ) {
                    cont++;
                    infra.app_in.adiciona(clock, m);
                }
            }
        infra.debug("p"+id+" entregara "+cont+" msgs do bloco "+minimo);
        /*
         *  Atualiza Unstable Messages retirando mensagens estáveis
         * 
         */
        java.util.ArrayList remover = new java.util.ArrayList();
        for (int i=1; i<= bufferDeMensagensInstaveis.size();i++) {
            m = (Message) bufferDeMensagensInstaveis.get(i-1);
            if ( (m.relogioLogico <= minimoEstavel) ) {
                remover.add(m);
            }
        }
        bufferDeMensagensInstaveis.removeAll(remover);
        
        for (int i=1; i<=bufferDeMensagens.size();i++) {
            m = (Message) bufferDeMensagens.get(i-1);
            if ( (m.relogioLogico > minimoEstavel) && !(bufferDeMensagensInstaveis.contains(m))  ) {
                bufferDeMensagensInstaveis.add(m);
            }
        }

        if (cont==0) return;

        cont=0;
        /*
         *  Coloca no buffer de entrega as mensagens completas
         */
        /*
        while (chave.iterator().hasNext()) {
            od = (Integer) chave.first();
            m = (Mensagem) ordenar.get(od);

            bufferDeMensagens.remove((Object) m);
            cont++;
            if (chave.iterator().hasNext()) od = (Integer) chave.iterator().next();
        }
         *
         */
        remover.clear();
        for (int i=0; i< bufferDeMensagens.size();i++) {
            m = (Message) bufferDeMensagens.get(i);
            if ( (m.relogioLogico <= minimo) ) {
                remover.add(m);
                cont++;
            }
        }
        bufferDeMensagens.removeAll(remover);

        infra.debug("p"+id+" entregou "+cont+" msgs do bloco "+minimo);
    }
        
        /* 
         *   Este evento pode ser sobrecarregado pela ação específica 
         *   do protocolo, execute a recepção de mensagens
         */
        public void receive(Message msg) {

            Content_Unstable uC;
            Consensus c;
            int clock = (int)infra.clock.value();
            switch (msg.tipo) {
                case TIMEDCB_APP:                    
                case TIMEDCB_TS:
                    if (msg.relogioLogico > BM[msg.remetente]) { 
                            BM[msg.remetente] = msg.relogioLogico;
                        }
                    if ( ((Content_TimedCB) msg.conteudo).LCB > LCB[msg.remetente]) {
                            LCB[msg.remetente] = ((Content_TimedCB) msg.conteudo).LCB;
                        }

                    acks[msg.remetente].rrecvTime = msg.tempoRecepcao;
                    acks[msg.remetente].lsendTime = msg.relogioFisico;

                    //ultimaMsgTimeStamp[msg.remetente] = msg.relogioFisico;

                    RECV = maximo(RECV, msg.relogioLogico);
                    bufferDeMensagens.add(msg);
                    registraBloco(msg.relogioLogico, msg.remetente, msg.relogioFisico);
                    verificaCompletudeCausalBlock();

                    String saida = "p"+id;
                    for (int i=0;i<infra.nprocess;i++)
                        saida = saida+" "+BM[i];
                    saida=saida+" RECV "+RECV+" SENT "+SENT+ " LSB "+obtemMinimoEstavel()+" LCB "+obtemMinimo();
                    infra.debug(saida);

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
                            registraBloco(m.relogioLogico, m.remetente, m.relogioFisico);
                        }
                        informaBlocoPosConsenso(c.numero);
                        bloquearEntrega = false;
                        verificaCompletudeCausalBlock();
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
                    }
            }
            
        }
        
        /*
         *  Altera o coordenador do consenso
         */
        void rotacionaCoordenador(int i) {
            int clock = (int)infra.clock.value();
            Consensus[i].alteraRound(Consensus[i].getRound()+1);
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