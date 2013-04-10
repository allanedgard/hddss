package br.ufba.lasid.jds.prototyping.hddss.instances;

import br.ufba.lasid.jds.prototyping.hddss.Message;
import br.ufba.lasid.jds.prototyping.hddss.Randomize;
import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport;
import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport.Variable;
import br.ufba.lasid.jds.prototyping.hddss.SimulatedAgent;
import br.ufba.lasid.jds.prototyping.hddss.Simulator;

public class Agent_AdaptConsensus extends SimulatedAgent {
     
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
        double minProb;
        double maxProb;
        int logicalClock;
        long lastTimeSent;
        int payloadSize;
        final int TIMEDCB_APP = 4;
        final int TIMEDCB_TS = 5;
        final int CONSENSUS_REQUEST = 6;
        final int CONSENSUS_P1 = 8;
        final int CONSENSUS_P2 = 9;
        final int DECIDED = 10;
        final int DOWN = 11;
        final int UNCERTAIN = 12;
                
        int RECV;
        int SENT;
        
        int stableMode;
        
        int minExpiredBlock = Integer.MAX_VALUE;
        int [] BM;
        int [] LCB;
        Consensus [] consensusArray;
        int [] scheduler;
        int lastBlock;
        java.util.TreeMap blocks;
        java.util.ArrayList msgBuffer;
        java.util.ArrayList unstableMsgBuffer;
        java.util.ArrayList expiredBlocks;
        java.util.TreeMap schedule;
        IntegerSet proposedView;
                
        IntegerSet down;
        IntegerSet live;
        IntegerSet uncertain;
        IntegerSet view;
        Content_Acknowledge [] acks;
                
        boolean blockingDelivery;
        boolean consensus;
        
        Randomize r;
        
        public Agent_AdaptConsensus() {
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
            int finalTime = getInfra().context.get(RuntimeSupport.Variable.FinalTime).<Integer>value();
            schedule = new java.util.TreeMap();
            stableMode = 0;
            logicalClock = 0;
            BM = new int[getInfra().nprocess];
            LCB = new int[getInfra().nprocess];
            payloadSize = 0;
            
            r = new Randomize(this.getAgentID());
            scheduler = new int[finalTime*4];
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
            view = new IntegerSet();
            
            proposedView = new IntegerSet();
            
            initializeSets();
                       
            // Consensos que podem ser mantidos por bloco
            consensusArray = new Consensus[finalTime*2];
            acks = new Content_Acknowledge[getInfra().nprocess];
            // Inicia matriz de blocos e matriz de Last Complete Blocks
            for (int i = 0;i<getInfra().nprocess;i++) {
                BM[i]=-1;
                LCB[i]=-1;
                acks[i] = new Content_Acknowledge();
            }
            
        }

        public void initializeSets() {
            /*
             *  Inicia conjuntos, incluindo todos os processos 
             *  como LIVE na visao!
             */ 
            for (int i=0;i<getInfra().nprocess;i++) {
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

        public void setPayloadSize(String dt) {
            payloadSize = Integer.parseInt(dt);
        }

        public void setDeltaMin(String dt) {
            delta = Integer.parseInt(dt);
        }

        public void setMaxProb(String dt) {
            maxProb = Double.parseDouble(dt);
        }

        
        public void seMinProb(String dt) {
            minProb = Double.parseDouble(dt);
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
       
       void blockRegister(int blocknumber, int sender, long ti) {
                int TC;
                int finalTime = getInfra().context.get(RuntimeSupport.Variable.FinalTime).<Integer>value();
                int DESVIO = getInfra().context.get(RuntimeSupport.Variable.MaxDeviation).<Integer>value();
                
                Integer block = new Integer(blocknumber);
                long clock = getInfra().clock.value();

                String mode = getInfra().context.get(RuntimeSupport.Variable.Mode).<String>value();
                double ro = getInfra().context.get(RuntimeSupport.Variable.ClockDeviation).<Double>value();
                int maxro = getInfra().context.get(RuntimeSupport.Variable.MaxClockDeviation).<Integer>value();
                
                if (!blocks.containsKey(block)) {
                    /*
                     *  Cria o bloco!
                     */
                        if (block > lastBlock)
                            lastBlock = block;
                        blocks.put(block, clock);
                        if (mode.equals("t")) {
                            if (sender == getAgentID()) {
                                TC = (int) ( (clock+2 * getDeltaMax() + getMaxTS()) + DESVIO+1);
                            }
                            else
                                TC = (int) ( (clock+2 * getDeltaMax() + getMaxTS()-getDeltaMin()) + DESVIO+1);
                        } else {
                            if (sender == getAgentID()) {
                                TC = (int) ( (clock+2 *getDeltaMax() + getMaxTS() )* (1+ro*maxro))+1;
                            }
                            else
                                TC = (int) ( (clock+2 * getDeltaMax() + getMaxTS()-getDeltaMin())* (1+ro*maxro))+1;
                        }

                        /*  
                         *  Escalona o timeout do bloco!
                         *
                         */
                        
                        if (TC <= finalTime)
                                if (scheduler[TC] < blocknumber) {
                                    scheduler[TC] = blocknumber;
                                    //infra.debug("p"+ID+" bloco "+blocknumber+" expira em "+TC+" criado em "+clock+" sender = "+sender);
                                }
                        /*
                                if (verAgenda(TC) < numbloco) {
                                    alterarAgenda(TC,numbloco);
                                    debug("p"+ID+" bloco "+numbloco+" expira em "+TC+" criado em "+clock+" sender = "+sender);
                                }*/
                        
                }
        }
       
    int consensusNumber = 0;
    final int CONSENSUS_MAX = 10000;
    int timeout=0;
    int proposedValueConsensus[] = new int[CONSENSUS_MAX];
    int decidedValueConsensus[] = new int[CONSENSUS_MAX];
    long startConsensus[] = new long[CONSENSUS_MAX];
    long finalConsensus[] = new long[CONSENSUS_MAX];
    
           
    @Override
        public void execute() {
            long clock = getInfra().clock.value();
            
            /*
             *  execute usa o timedCB apenas para poder 
             *  implementar o detector de defeitos
             *  por simplificacao, o ID=0 gera os blocos novos
             *  e os demais respondem por meio do time-silence
             */

            /*  Falta implantar inicio e fim do consenso
             * 
             */
            
            
            if (this.getAgentID()==1) {
                if (proposedValueConsensus[consensusNumber]==0) {
                    proposedValueConsensus[consensusNumber] = this.getAgentID();
                    sendGroupMsg(clock, CONSENSUS_REQUEST, 
                            new Integer(proposedValueConsensus[consensusNumber]),
                                consensusNumber, true);
                    startConsensus[consensusNumber] = clock;
                    
                    }
                 /*       ((cont==getInfra().getNumberOfProcess()) || (getInfra().clock.value()  - timeout > DELTA) ) && (sequencia<TOTAL)) {
                    //System.out.println("Enviando em "+getInfra().clock.value());
                    sequencia++;
                    sendGroupMsg(APP, "stuff", sequencia, true);
                    timeout = getInfra().clock.value() + DELTA;
                    cont=0;*/
                }   
            
            
            
            //System.out.println("myID"+this.getAgentID());
            if (this.getAgentID()==1) {

                if (getInfra().clock.value()  - SENT > 4*DELTA) {
                    /*
                    System.out.println("enviando...");
                    System.out.println("SENT="+ SENT);
                    System.out.print("DIFF="+ (getInfra().clock.value()  - SENT));
                    System.out.println("LIMITE="+4*DELTA);*/
                    logicalClock ++;
                    SENT = logicalClock;
                    sendGroupMsg(clock, TIMEDCB_APP, 
                            new Content_TimedCB("payload", LCB[getAgentID()], 
                                acks, payloadSize), 
                                    logicalClock, true );
                    blockRegister(logicalClock, getAgentID(), clock);}
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

                        sendGroupMsg(clock, TIMEDCB_TS, new Content_TimedCB("time-silent", LCB[getAgentID()], acks), logicalClock );
                        blockRegister(logicalClock, getAgentID(), clock);
                        
                        boolean isTimely=false;
                        for (int j=0; j<getInfra().nprocess;j++){
                            if (view.exists(j)) {
                                if (getScenario().
                                    getContainer().
                                        getNetwork().
                                            informChannel( (int) this.getAgentID(), j) 
                                                instanceof
                                                    br.ufba.lasid.jds.prototyping.hddss.ChannelDeterministic) {
                                    isTimely=true;
                                }
                            }
                        }
                        if (isTimely == false) {
                            if (live.exists((int) this.getAgentID())) {
                                // MUDA O PROCESSO PARA UNCERTAIN
                                live.remove((int) this.getAgentID());
                                uncertain.add((int) this.getAgentID());
                                this.sendGroupMsg(clock, UNCERTAIN, (int) this.getAgentID(), 0, false);                                
                            }
                        }
                
                        
                        
                }
            }
                       
            if (scheduler[(int) clock] != -1) {
                 if (getMin() < scheduler[(int) clock]) {
                    for (int i = getMin()+1; i<=scheduler[(int) clock];i++) {
                        //infra.debug("p"+Integer.toString(ID)+": timeout in block "+Integer.toString(i) + " in "+Integer.toString(clock));
                        notifyExpiredBlocks(i);
                        IntegerSet x = retornaExpirados(i);
                        int vetor[] = x.toVector();
                        for (int j=0;j<vetor.length;j++) {
                            
                            if (getScenario().
                                   getContainer().
                                      getNetwork().
                                         informChannel( (int) this.getAgentID(), j) 
                                            instanceof
                                               br.ufba.lasid.jds.prototyping.hddss.ChannelDeterministic) {
                                // RETIRA O PROCESSO DE LIVE E PASSA PARA DOWN (CERTEZA NA FALHA)
                                if ( live.exists(j) ) {
                                    down.add(j);
                                    live.remove(j);
                                    view.remove(j);
                                    this.sendGroupMsg(clock, DOWN, j, 0, false);
                                }
                                
                                // INCLUIR INFORMAR AOS DEMAIS DA FALHA
                                // INCLUIR INFORMAR AOS DEMAIS QUANDO UM PROCESSO SE TORNA UNCERTAIN
                            }
                        }
                    }
                 }
            }
        }
        
        /*
         *  Envia mensagem a grupo
         */
        public void sendGroupMsg(long clock, int type, Object value, int LC) {
            for (int j=0; j<getInfra().nprocess;j++){
                
                if(value instanceof Content_TimedCB){
                    ((Content_TimedCB)value).vack[j].rsendTime = clock;
                }
                this.createMessage(clock, this.getAgentID(), j, type, value, LC);
            }
        }
        
        public void sendGroupMsg(long clock, int type, Object value, int LC, boolean pay) {
            for (int j=0; j<getInfra().nprocess;j++){
                if(value instanceof Content_TimedCB){
                    ((Content_TimedCB)value).vack[j].rsendTime = clock;
                }

                this.createMessage(clock, this.getAgentID(), j, type, value, LC, pay);
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
        for (int i=0; i<getInfra().nprocess;i++){
            if (view.exists(i)) {
                if (BM[i] < min)
                    min = BM[i]; }
            // else System.nic_out.println("O processo "+i+" não existe na visao de "+ID);
        }
        return min;
     }
         
     public int getStableMin() {
        int min = Integer.MAX_VALUE;
        for (int i=0; i<getInfra().nprocess;i++){
            if (view.exists(i))
                if (LCB[i] < min)
                    min = LCB[i];
        }
        return min;
     }
     
     /*
      *  Informa quais os processos responsáveis pela expiração do bloco
      */
     public IntegerSet retornaExpirados(int bl) {
        IntegerSet x = new IntegerSet();
        for (int i=0; i<getInfra().nprocess;i++){
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
        int n = getInfra().nprocess;
        int min = getMin();
        LCB[getAgentID()] = min;
        long clock = getInfra().clock.value();
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
                // System.nic_out.println("p"+ID+" min="+minimo+" ts log = "+m.relogioLogico);
                if ( (m.logicalClock <= min)  ) {
                    cont++;
                    if (m.type == TIMEDCB_APP)
                        this.preDeliver(m);
                }
            }
        //infra.debug("p"+ID+" delivers "+cont+" msgs of block "+min);
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

        //infra.debug("p"+ID+" delivers "+cont+" msgs of block "+min);
    }
        
        public void receive(Message msg) {

            Content_Unstable uC;
            Consensus c;
            int j;
            long clock = getInfra().clock.value();
            switch (msg.type) {
                case DOWN:
                    j = ((Integer) msg.getContent()).intValue();
                    if ( live.exists(j) ) {
                                    down.add(j);
                                    live.remove(j);
                                    view.remove(j);
                    }
                    break;
                case UNCERTAIN:
                    j = ((Integer) msg.getContent()).intValue();
                    if ( live.exists(j) ) {
                                    uncertain.add(j);
                                    live.remove(j);
                    }                    
                    break;                                
                case TIMEDCB_APP:                    
                case TIMEDCB_TS:
                    if (msg.logicalClock > BM[msg.sender]) {
                            BM[msg.sender] = msg.logicalClock;
                        }
                    if ( ((Content_TimedCB) msg.content).LCB > LCB[msg.sender]) {
                            LCB[msg.sender] = ((Content_TimedCB) msg.content).LCB;
                        }

                    acks[msg.sender].rrecvTime = msg.receptionTime;
                    acks[msg.sender].lsendTime = msg.physicalClock;

                    //ultimaMsgTimeStamp[msg.remetente] = msg.relogioFisico;

                    RECV = max(RECV, msg.logicalClock);
                    msgBuffer.add(msg);
                    blockRegister(msg.logicalClock, msg.sender, msg.physicalClock);
                    checkCompletion();

                    String output = "p"+getAgentID();
                    for (int i=0;i<getInfra().nprocess;i++)
                        output = output+" "+BM[i];
                    output=output+" RECV "+RECV+" SENT "+SENT+ " LSB "+getStableMin()+" LCB "+getMin();
                    //infra.debug(output);

                    break;
                case CONSENSUS_REQUEST:
                    consensusArray[msg.logicalClock] = startConsensus(msg);
                    if (consensusArray[msg.logicalClock].getRound()%(getInfra().nprocess)  == getAgentID()) {                               
                        sendGroupMsg(clock, CONSENSUS_P1,consensusArray[msg.logicalClock], logicalClock);
                    }
                    break;
                case CONSENSUS_P1:
                    c = (Consensus) msg.content;
                    // Mensagem do mesmo round
                    if (consensusArray[c.number]==null)
                        consensusArray[c.number] = c;
                    
                    if (c.getRound()==(consensusArray[c.number].getRound())) {
                        if (msg.sender == c.getRound() % getInfra().nprocess) {
                            consensusArray[c.number].estimated = c.estimated;
                            sendGroupMsg(clock, CONSENSUS_P2,consensusArray[c.number], logicalClock);
                        }
                    }
                    else if  (c.getRound()<(consensusArray[c.number].getRound())){
                                c.alteraRound(consensusArray[c.number].getRound() );
                                if (msg.sender == c.getRound() % getInfra().nprocess) {
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
                        consensusArray[c.number] = c;
                    
                    if (!consensusArray[c.number].gotQuorum) {
                        consensusArray[c.number].quorum.add(msg.sender);
                        // adicionaREC(c.numero, (UnstableContent) c.estimado);
                        if ( (consensusArray[c.number].rec == null) && (c!=null))
                                consensusArray[c.number].rec = c.estimated;
                        if (gotConsensusQuorum(c.number)) {
                            if ( c.estimated == null)
                                consensusArray[c.number].noneREC = true;
                            if ( consensusArray[c.number].rec == null)
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
                        decidedValueConsensus[c.number] = (Integer) c.rec;
                        if (consensusNumber == c.number) {
                            finalConsensus[consensusNumber] = clock;
                            if (this.getAgentID()==1) {
                                this.getReporter().stats("consensus delay", finalConsensus[consensusNumber] - startConsensus[consensusNumber]);
                                this.getReporter().count("consensus total number");
                            }
                            consensusNumber++;

                        }
                        /*
                        System.out.println("Consenso " +c.number +" Obtido em "+clock);
                        System.out.println("valor = "+decidedValueConsensus[c.number]);
                        System.out.println("");
                        */ 
                        }
                    }
            }
            

        
        /*
         *  Altera o coordenador do consenso
         */
        void rotacionaCoordenador(int i) {
            long clock = getInfra().clock.value();
            consensusArray[i].alteraRound(consensusArray[i].getRound()+1);
            if (consensusArray[i].getRound()%getInfra().nprocess == getAgentID())
                sendGroupMsg(clock, CONSENSUS_P1,consensusArray[i], logicalClock);
        }
        
        
        /*
         *  Cria um novo consenso
         */
        Consensus startConsensus(Message m) {
            Consensus c = new Consensus(m.logicalClock,0,m.content);
            return c;
        }

        int max(int a, int b) {
            if (a>b)
                return a;
            else
                return b;
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