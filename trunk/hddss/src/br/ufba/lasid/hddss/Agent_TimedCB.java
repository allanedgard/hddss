package br.ufba.lasid.hddss;

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
        int logicalClock;
        int lastTimeSent;
        final int TIMEDCB_APP = 4;
        final int TIMEDCB_TS = 5;
        final int CHANGE_VIEW_REQUEST = 6;
        final int UNSTABLE = 7;
        final int CONSENSUS_P1 = 8;
        final int CONSENSUS_P2 = 9;
        final int DECIDED = 10;
        int RECV;
        int SENT;
        
        int stableMode;
        
        int minExpiredBlock = Integer.MAX_VALUE;
        int [] BM;
        int [] LCB;
   //     char [] [] BM_Matrix;
        Consensus [] consensusArray;
        int [] scheduler;
        int [] ultimaMsgTimeStamp;
        Content_Acknowledge [] acks;
        int lastBlock;
        java.util.TreeMap blocks;
        java.util.ArrayList msgBuffer;
        java.util.ArrayList unstableMsgBuffer;
        java.util.ArrayList expiredBlocks;
        java.util.ArrayList unstableSentMsgs;
        java.util.ArrayList allUnstableMsgs;
        java.util.TreeMap schedule;
        IntegerSet proposedView;
                
        IntegerSet down;
        IntegerSet live;
        IntegerSet uncertain;
        IntegerSet suspected;
        IntegerSet view;
                
        boolean blockingDelivery;
        boolean consensus;
        
        Randomize r;
        
        Agent_TimedCB() {
        super();
        }
        
        int verAgenda(int t) {
            int B = -1;
            if ( schedule.containsKey(t) )
                    B = (Integer) schedule.get(t);
            return B;
        }
        
        void alterarAgenda(int t, int B) {
            if (B==-1) {
                schedule.remove(t);
            }
            else if (verAgenda(t)<B) {
                schedule.remove(t);
                schedule.put(t, B);
            }
        }
        

    @Override
        public void setup() {
            schedule = new java.util.TreeMap();
            stableMode = 0;
            logicalClock = 0;
            BM = new int[infra.nprocess];
            LCB = new int[infra.nprocess];
            ultimaMsgTimeStamp = new int[infra.nprocess];

            acks = new Content_Acknowledge[infra.nprocess];
            
            r = new Randomize(id);
            scheduler = new int[infra.context.tempofinal*2];
 //           BM_Matrix = new char[controle.n][controle.tempofinal*2];
            for (int j = 0;j<scheduler.length ;j++) {
                    scheduler[j]=-1;
            }   
            
            /*  Constrói os blocos básicos:
             *  - Buffer de mensagens recebidas
             *  - blocos de mensagens
             *  - lista de blocos que foram expirados
             *  - mensagens instáveis
             */
            msgBuffer = new java.util.ArrayList();
            unstableMsgBuffer =  new java.util.ArrayList();
            blocks = new java.util.TreeMap();
            expiredBlocks = new java.util.ArrayList();
            unstableSentMsgs = new java.util.ArrayList();
            
            /*
             *  Ultimo bloco completo e ultimo tempo de envio
             */
            lastTimeSent = -1;  // Último tempo de envio
            lastBlock = -1;    // Ultimo Bloco Completo
            SENT = -1;
            RECV = -1;
            
            /* Flags para bloquear a entrega 
             * e startup o consenso
             */ 
            blockingDelivery = false;
            consensus = false;
            
            /* Conjuntos a serem mantidos pelos 
             * Detectores de Estados e de Defeitos
             */
            down = new IntegerSet();
            live = new IntegerSet();
            uncertain = new IntegerSet();
            suspected = new IntegerSet();
            view = new IntegerSet();
            
            proposedView = new IntegerSet();
            
            initializeSets();
            
            // Mensagens instáveis que serão usadas no consenso
            allUnstableMsgs =  new java.util.ArrayList();
            
            // Consensos que podem ser mantidos por bloco
            consensusArray = new Consensus[infra.context.tempofinal*2];
            
            // Inicia matriz de blocos e matriz de Last Complete Blocks
            for (int i = 0;i<infra.nprocess;i++) {
                BM[i]=-1;
                LCB[i]=-1;
                acks[i] = new Content_Acknowledge();
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

        public void initializeSets() {
            /*
             *  Inicia conjuntos, incluindo todos os processos 
             *  como LIVE na visao!
             */ 
            for (int i=0;i<infra.nprocess;i++) {
                live.add(i);
                view.add(i);
            }
        }

        /*
         *  Definem parâmetros do algoritmo
         */
        
        public void setDeltaMax(String dt) {
            DELTA = Integer.parseInt(dt);
        }

        public void setStableMode(String dt) {
            stableMode = Integer.parseInt(dt);
        }
        
        public void setDeltaMin(String dt) {
            delta = Integer.parseInt(dt);
        }

        public void setTimeSilence(String p) {
            ts = Integer.parseInt(p);
            tsmax = ts;
        }

        public void setPacketGenerationProb (String po) {
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
       
       void blockRegister(int blocknumber, int sender, int ti) {
                int TC;
                Integer block = new Integer(blocknumber);
                int clock = (int)infra.clock.value();
                if (!blocks.containsKey(block)) {
                    /*
                     *  Cria o bloco!
                     */
                        if (block > lastBlock)
                            lastBlock = block;
                        blocks.put(block, clock);
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
                                if (scheduler[TC] < blocknumber) {
                                    scheduler[TC] = blocknumber;
                                    infra.debug("p"+id+" bloco "+blocknumber+" expira em "+TC+" criado em "+clock+" sender = "+sender);
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

                logicalClock ++;        // Ajusta o relógio lógico
                //LastTimeSent = clock;   // Registra clock do ultimo envio
                SENT = logicalClock;    // Registra numero do bloco do ultimo envio

                sendGroupMsg(clock, TIMEDCB_APP, new Content_TimedCB("payload", LCB[id], acks), logicalClock, true );
                blockRegister(logicalClock, id, clock);
            }
            
            /*
             *  Se há silêncio, invoca o mecanismo de time-silence
             *  o mecanismo é invocado mesmo sem blocos completos?
             */
            
            if ( (RECV > SENT) || (blockingDelivery) ) {
                if (clock - lastTimeSent >= ts) {

                        lastTimeSent = clock;
                        logicalClock = max(logicalClock+1,lastBlock);
                        SENT = logicalClock;

                        sendGroupMsg(clock, TIMEDCB_TS, new Content_TimedCB("time-silent", LCB[id], acks), logicalClock );
                        blockRegister(logicalClock, id, clock);
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
            
            if (scheduler[clock] != -1) {
                 if (getMin() < scheduler[clock]) {
                    for (int i = getMin()+1; i<=scheduler[clock];i++) {
                        infra.debug("p"+Integer.toString(id)+": timeout in block "+Integer.toString(i) + " in "+Integer.toString(clock));
                        notifyExpiredBlocks(i);
                        IntegerSet x = retornaExpirados(i);
                        if (!blockingDelivery) {
                            sendGroupMsg(clock, CHANGE_VIEW_REQUEST, Integer.toString(i), logicalClock);
                            blockingDelivery=true;
                        }
                        down.add(live.intersection(x));
                        live.remove(x);
                        suspected.clean();
                        suspected.add(uncertain.intersection(x));
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
        public void sendGroupMsg(int clock, int type, Object value, int LC) {
            for (int j=0; j<infra.nprocess;j++){
                
                if(value instanceof Content_TimedCB){
                    ((Content_TimedCB)value).vack[j].rsendTime = clock;
                }
                this.criamensagem(clock, this.id, j, type, value, LC);
            }
        }
        
        public void sendGroupMsg(int clock, int type, Object value, int LC, boolean pay) {
            for (int j=0; j<infra.nprocess;j++){
                if(value instanceof Content_TimedCB){
                    ((Content_TimedCB)value).vack[j].rsendTime = clock;
                }

                this.criamensagem(clock, this.id, j, type, value, LC, pay);
            }
        }
        
        
        
    /*
     *  Dado um bloco expirou inclui o mesmo na lista de blocos expirados
     */ 
    public void notifyExpiredBlocks(int bl) {
            expiredBlocks.add(bl);
            if (minExpiredBlock == Integer.MAX_VALUE)
                minExpiredBlock = bl;
     }

     /*
     *  Ao efetuar uma entrega atualiza o relogio logico
     */ 
     public void deliver(Message msg) {
            if (msg.logicalClock > logicalClock) {
                logicalClock = msg.logicalClock+1;
            }
     }

     /*
      *  Indica qual o ultimo bloco completo
      */ 
     public int getMin() {
        int min = Integer.MAX_VALUE;
        for (int i=0; i<infra.nprocess;i++){
            if (view.exists(i)) {
                if (BM[i] < min)
                    min = BM[i]; }
            // else System.nic_out.println("O processo "+i+" não existe na visao de "+id);
        }
        return min;
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
     
     public int getStableMin() {
        int min = Integer.MAX_VALUE;
        for (int i=0; i<infra.nprocess;i++){
            if (view.exists(i))
                if (LCB[i] < min)
                    min = LCB[i];
        }
        return min;
     }
     
     /*
      *  Ajusta a matriz de blocos com o resultado do consenso
      */
     public void informaBlocoPosConsenso(int x) {
        for (int i=0; i<infra.nprocess;i++){
            if (view.exists(i))
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
            if (view.exists(i))
                if (BM[i] < bl)   
                    x.add(i);
        }
        return x;
     }     

     /*
      *  Veficica se blocos estão completos e entrega!
      *  Atualiza o LCB
      *  Atualiza as mensagens instáveis
      */
     void checkCompletion() {
        int n = infra.nprocess;
        int min = getMin();
        LCB[id] = min;
        int clock = (int)infra.clock.value();
        int minStable = getStableMin();
        java.util.TreeSet key = new java.util.TreeSet();
        java.util.TreeMap sorting = new java.util.TreeMap();
        int order;
        Integer od;
        Message m;
        if (blockingDelivery) return;
        
        if (stableMode == 1)
            min = minStable;

        java.util.Collections.sort(msgBuffer, new java.util.Comparator() {
             public int compare(Object o1, Object o2) {
                 Message msg1 = (Message) o1;
                 Message msg2 = (Message) o2;
                 return     (msg1.logicalClock - msg2.logicalClock) != 0 ?
                            (msg1.logicalClock - msg2.logicalClock) :
                            (msg1.sender - msg2.sender);

             }
        });
        
        //if (LogicalClock < minimo) LogicalClock = minimo;
        int cont=0;
        for (int i=0; i<msgBuffer.size();i++) {
                m = (Message) msgBuffer.get(i);
                // System.nic_out.println("p"+id+" min="+minimo+" ts log = "+m.relogioLogico);
                if ( (m.logicalClock <= min)  ) {
                    cont++;
                    infra.app_in.add(clock, m);
                }
            }
        infra.debug("p"+id+" delivers "+cont+" msgs of block "+min);
        /*
         *  Atualiza Unstable Messages retirando mensagens estáveis
         * 
         */
        java.util.ArrayList remover = new java.util.ArrayList();
        for (int i=1; i<= unstableMsgBuffer.size();i++) {
            m = (Message) unstableMsgBuffer.get(i-1);
            if ( (m.logicalClock <= minStable) ) {
                remover.add(m);
            }
        }
        unstableMsgBuffer.removeAll(remover);
        
        for (int i=1; i<=msgBuffer.size();i++) {
            m = (Message) msgBuffer.get(i-1);
            if ( (m.logicalClock > minStable) && !(unstableMsgBuffer.contains(m))  ) {
                unstableMsgBuffer.add(m);
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
        for (int i=0; i< msgBuffer.size();i++) {
            m = (Message) msgBuffer.get(i);
            if ( (m.logicalClock <= min) ) {
                remover.add(m);
                cont++;
            }
        }
        msgBuffer.removeAll(remover);

        infra.debug("p"+id+" delivers "+cont+" msgs of block "+min);
    }
        
        /* 
         *   Este evento pode ser sobrecarregado pela ação específica 
         *   do protocolo, execute a recepção de mensagens
         */
        public void receive(Message msg) {

            Content_Unstable uC;
            Consensus c;
            int clock = (int)infra.clock.value();
            switch (msg.type) {
                case TIMEDCB_APP:                    
                case TIMEDCB_TS:
                    if (msg.logicalClock > BM[msg.sender]) {
                            BM[msg.sender] = msg.logicalClock;
                        }
                    if ( ((Content_TimedCB) msg.content).LCB > LCB[msg.sender]) {
                            LCB[msg.sender] = ((Content_TimedCB) msg.content).LCB;
                        }

                    acks[msg.sender].rrecvTime = msg.tempoRecepcao;
                    acks[msg.sender].lsendTime = msg.physicalClock;

                    //ultimaMsgTimeStamp[msg.remetente] = msg.relogioFisico;

                    RECV = max(RECV, msg.logicalClock);
                    msgBuffer.add(msg);
                    blockRegister(msg.logicalClock, msg.sender, msg.physicalClock);
                    checkCompletion();

                    String output = "p"+id;
                    for (int i=0;i<infra.nprocess;i++)
                        output = output+" "+BM[i];
                    output=output+" RECV "+RECV+" SENT "+SENT+ " LSB "+getStableMin()+" LCB "+getMin();
                    infra.debug(output);

                    break;
                case CHANGE_VIEW_REQUEST:
                    if ( unstableSentMsgs.contains(msg.content) )
                            break;
                    unstableSentMsgs.add(msg.content);
                    blockingDelivery = true;
                    int b = Integer.parseInt((String) msg.content);
                    uC = new Content_Unstable(b, unstableMsgBuffer, msg.sender, down);
                    sendGroupMsg(clock, UNSTABLE, uC, logicalClock);
                    break;
                case UNSTABLE:
                    if (!consensus) {
                        uC = (Content_Unstable) msg.content;
                        // DOWN = DOWN U DOWN PERCEBIDO!
                        down.add(uC.down);
                        addView(uC);
                        if (gotUnstableMsgs() ) {
                            consensus = true;
                            down.add(live);
                            live.add(down);
                            down.remove(proposedView);
                            uC.down.clean();
                            uC.down.add(down);
                            uC.visaoProposta.clean();
                            uC.visaoProposta.add(this.proposedView);

                            consensusArray[uC.bloco] = startConsensus(uC);
                            
                            if (consensusArray[uC.bloco].getRound()%infra.nprocess == id)
                                sendGroupMsg(clock, CONSENSUS_P1,consensusArray[uC.bloco], logicalClock);
                        
                        }
                    }
                    break;
                case CONSENSUS_P1:
                    c = (Consensus) msg.content;
                    // Mensagem do mesmo round
                    if (consensusArray[c.number]==null)
                        consensusArray[c.number] = startConsensus( (Content_Unstable) c.estimated );
                    
                    if (c.getRound()==(consensusArray[c.number].getRound())) {
                        if (msg.sender == c.getRound() % infra.nprocess) {
                            consensusArray[c.number].estimated = c.estimated;
                            sendGroupMsg(clock, CONSENSUS_P2,consensusArray[c.number], logicalClock);
                        }
                    }
                    else if  (c.getRound()<(consensusArray[c.number].getRound())){
                                c.alteraRound(consensusArray[c.number].getRound() );
                                if (msg.sender == c.getRound() % infra.nprocess) {
                                   consensusArray[c.number].estimated = c.estimated;
                                    sendGroupMsg(clock, CONSENSUS_P2,consensusArray[c.number], logicalClock);
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
                    if (consensusArray[c.number]==null)
                        consensusArray[c.number] = startConsensus( (Content_Unstable) c.estimated );
                    
                    if (!consensusArray[c.number].gotQuorum) {
                        consensusArray[c.number].quorum.add(msg.sender);
                        // adicionaREC(c.numero, (UnstableContent) c.estimado);
                        if ( ((Content_Unstable) c.estimated).tamanho() != 0)
                                consensusArray[c.number].rec = c.estimated;
                        if (gotConsensusQuorum(c.number)) {
                            if ( ((Content_Unstable) c.estimated).tamanho() == 0)
                                consensusArray[c.number].noneREC = true;
                            if ( ( (Content_Unstable) consensusArray[c.number].rec).conteudo.size() == 0)
                            {
                                if (consensusArray[c.number].noneREC) {
                                /*      - se sim, se a decisão é somente {_|_} 
                                */
                                rotacionaCoordenador(c.number);
                                }
                                else {
                                    /*      - se sim, se a decisão é {v, _|_} 
                                    */
                                    rotacionaCoordenador(c.number);
                                    consensusArray[c.number].estimated = consensusArray[c.number].rec;
                                }
                            } 
                            else {
                                /*      - se sim, se a decisão é {v} 
                                */                        
                                sendGroupMsg(clock, DECIDED,consensusArray[c.number], logicalClock);
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
                    if (consensusArray[c.number].active) {
                        consensusArray[c.number].active = false;
                        consensusArray[c.number].estimated = c.rec;
                        uC  =  (Content_Unstable) consensusArray[c.number].estimated;
                        java.util.ArrayList x = (java.util.ArrayList) uC.conteudo;
                        for (int i=0;i<x.size();i++) {
                            Message m = (Message) x.get(i);
                            if (m.logicalClock > BM[m.sender])
                                BM[m.sender] = m.logicalClock;
                            msgBuffer.add(m);
                            blockRegister(m.logicalClock, m.sender, m.physicalClock);
                        }
                        informaBlocoPosConsenso(c.number);
                        blockingDelivery = false;
                        checkCompletion();
                        down.add(live);
                        live = live.intersection(uC.visaoProposta);
                        uncertain = uncertain.intersection(uC.visaoProposta);
                        down.remove(uC.visaoProposta);
                        suspected.clean();
                        view.clean();
                        view.add(proposedView);
                        System.out.println("Consenso Obtido em "+clock);
                        System.out.println("Visao em p"+id);
                        for (int i=0; i<infra.nprocess; i++)
                            if (view.exists(i) )
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
            consensusArray[i].alteraRound(consensusArray[i].getRound()+1);
            if (consensusArray[i].getRound()%infra.nprocess == id)
                sendGroupMsg(clock, CONSENSUS_P1,consensusArray[i], logicalClock);
        }
        
        
        /*
         *  Cria um novo consenso
         */
        Consensus startConsensus(Content_Unstable uC) {
            Consensus c = new Consensus(uC.bloco,0,uC);
            return c;
        }

        int max(int a, int b) {
            if (a>b)
                return a;
            else
                return b;
        }
        
        /* 
         *  Cria uma nova visão com os processos que participaram
         */
        void addView(Content_Unstable uC) {
            if (!proposedView.exists(uC.id)) {
                proposedView.add(uC.id);
                infra.debug(proposedView.toString());
                for (int i = 0; i < uC.conteudo.size(); i++ )
                    if (!allUnstableMsgs.contains(uC.conteudo.get(i)))
                        allUnstableMsgs.add(uC.conteudo.get(i));
            }
        }
        
        /*
         *   Adiciona aos valores recebidos na construção do consenso
         */
        void addRec(int number, Content_Unstable uC) {
            for (int i = 0; i < uC.conteudo.size(); i++ )
                    if (! ( ((java.util.ArrayList) consensusArray[number].rec).contains(uC.conteudo.get(i))  )  )
                        ((java.util.ArrayList) consensusArray[number].rec).add(uC.conteudo.get(i));
        }
        
        /*
         *   Verifica se obteve todas as UnstableMensagens
         */
        boolean gotUnstableMsgs() {
            boolean liveOk;
            boolean uncertainOk;
            
            liveOk = (live.size()==live.intersection(proposedView).size());
            
            infra.debug("live ok? "+liveOk);
            infra.debug("tam live="+live.size());
            infra.debug("tam live inter View="+live.intersection(proposedView).size());
            infra.debug("live = "+live);
            infra.debug("view = "+proposedView);
            
            int contaUncertain=proposedView.intersection(uncertain).size();

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
        boolean gotConsensusQuorum(int i) {
            boolean liveOk;
            boolean uncertainOk;
            
            liveOk = (live.size()==live.intersection(consensusArray[i].quorum).size());
            
            int contaUncertain=consensusArray[i].quorum.intersection(uncertain).size();

            if (uncertain.size() >0) {
                float perc = contaUncertain / uncertain.size();
                if (perc>.5) {uncertainOk = true;} else {uncertainOk = false;};
            } else 
                uncertainOk = true;

            return (liveOk && uncertainOk);
        }
        
}