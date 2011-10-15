package br.ufba.lasid.jds.prototyping.hddss;

public class Agent_AmoebaSequencer extends SimulatedAgent {

        int Leader;         // Who is the Leader? Process Number
        boolean AmILeader;  // Am I the Leader? YES or NO
        int Sequential;     // Sequential Number        
        int finalTime;       // Final Simulation Time
                
        int LastACK;    // Last Message ACKnowledged
        int LastDLV;    // Last Message DELivered
        int SENT;    
        int DSENT;
        IntegerSet ACKS;
        IntegerSet DLVS;
        
        int DELTA;
        int delta;
        int ts;
        double prob;
        
        int LogicalClock;
        int LastTimeSent;
        
        /*
         *  CLASSES OF MESSAGES
         * 
         */
        final int REQ_SEQ = 11;
        final int APP = 12;
        final int ACK = 13;
        final int DLV = 14;
        
        int [] BM;
        int count;
        
        
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

            
        Randomize r;
        
        int [] timeout;
        int [] quorum;        
        
        Agent_AmoebaSequencer() {
        super();
        }
    
    @Override
        public void setup() {
            finalTime = infra.context.get(RuntimeSupport.Variable.FinalTime).<Integer>value();

            bloquearEntrega = false;
  
            Leader = 1;
            if (ID==Leader)
                AmILeader = true;
            
            r = new Randomize();
            msgs = new Buffer();
            
            Sequential = -1;
            SENT = 0;
            DSENT = 0;
            LastDLV = -1;
            LastACK = -1;
            ACKS = new IntegerSet();
            DLVS = new IntegerSet();
            
            /* Flags para bloquear a entrega 
             * e startup o consenso
             */ 
            bloquearEntrega = false;
            
            BM = new int[infra.nprocess];
            for (int i=0;i<infra.nprocess;i++)
                BM[i]=-1;
            
            quorum= new int[1000000];
            count = 0;
        }
        
        /*
         *  Parameters
         */
        
        public void setDeltaMax(String dt) {
            DELTA = Integer.parseInt(dt);
        }
        
        public int getDelta() {
            return DELTA;
        }        

        public void setTS(String dt) {
            ts = Integer.parseInt(dt);
        } 
        
        public void setPacketGenerationProb (String po) {
            prob = Double.parseDouble(po);
        }
        
    @Override
        public void startup(){

        }   
     
        
    @Override
        public void execute() {
            int clock = (int)infra.clock.value();
            Content_AmoebaSequencer ca = new Content_AmoebaSequencer(LastACK, "stuff");
            if ( (r.uniform() <= prob) && !bloquearEntrega && count < 10 && clock < .5*finalTime ) {
                SENT = clock;    // Registering clock of the last SENT 
                ca.ACKS.add(ACKS);
                this.createMessage(clock, ID, Leader, REQ_SEQ, ca, -1);
                ACKS.clean();
                count++;
            }
            if ( (clock - SENT >= ts) && (ACKS.size() >0 ) ) {
                        ca.ACKS.add(ACKS);
                        this.createMessage(clock, ID, Leader, ACK, ca, -1);
                        ACKS.clean();              
                        SENT = clock;
                        System.out.println("p"+ID+" FLUSHED ACKS = "+ACKS.size());
                    }
            if ( (AmILeader) && (clock - DSENT >= ts)&& (DLVS.size() >0 ) ) {
                            this.sendGroupMsg(clock, DLV, ca, ca.DLVS.min());
                            System.out.println("Enviando");
                            DLVS.clean();
                            DSENT = clock;
                        }
        }
        
        public void sendGroupMsg(int clock, int tipo, Object valor, int LC) {
            for (int j=0; j<infra.nprocess;j++) {
                this.createMessage(clock, this.ID, j, tipo, valor, LC);  }
        }
        
        public void sendGroupMsg(int clock, int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<infra.nprocess;j++) {
                this.createMessage(clock, this.ID, j, tipo, valor, LC, pay);
            }
        }

        public void relayGroupMsg(int clock, int i, int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<infra.nprocess;j++) {
                this.createMessage(clock, i, j, tipo, valor, LC, pay);
            }
        }        

        
        /* 
         *   Este evento pode ser sobrecarregado pela ação específica 
         *   do protocolo, execute a recepção de mensagens
         */
        
        void checkingQuorum(Message msg, Content_AmoebaSequencer ca ) {
                        int clock = (int) this.infra.clock.value();
                        //System.out.println("Sequencer: RECEIVING ACKS of p"+msg.sender+" = "+ca.ACKS);
                        for (int i=0;i<ca.ACKS.size();i++) {
                            int numSeq = ca.ACKS.toVector()[i];
                            quorum[numSeq]++;
                            
                            if ( quorum[numSeq] == infra.nprocess ) {
                                DLVS.add(numSeq);
                                ca.DLVS.add(DLVS);
                                System.out.println("being DELIVERING "+ca.DLVS);
                                System.out.println("seq "+numSeq+": count "+quorum[numSeq]);
                            }
                        }
                        
                        
                        if (clock - DSENT >= ts) {
                            this.sendGroupMsg(clock, DLV, ca, ca.DLVS.min());
                            DLVS.clean();
                            DSENT = clock;
                        } else
                            System.out.println("avoid DELIVERING "+DLVS);
                        
                
        }
                
        @Override
        public void receive(Message msg) {
            Content_Unstable uC;
            Consensus c;
            Content_AmoebaSequencer ca;
            int clock = (int)infra.clock.value();

            switch (msg.type) {
                case REQ_SEQ:
                    if (AmILeader) {
                        Sequential++;
                        ca = (Content_AmoebaSequencer) msg.content;
                        /*
                         *   PROCESSA OS EMBEDDED ACKS 
                         */
                        ca = (Content_AmoebaSequencer) msg.content;
                        this.checkingQuorum(msg, ca);
                        /*
                         *    ENVIA MSG
                         */
                        ca.LastDLV = LastDLV;
                        ca.LastACK = LastACK;
                        
                        msg.logicalClock = Sequential;
                        ca.contentMsg = msg;
                        ca.ACKS.clean();
                        //ca.DLVS.clean();
                        ca.DLVS.add(DLVS);
                        
                        quorum[Sequential]=0;
                        SENT = clock;
                        sendGroupMsg(clock, APP, ca, Sequential, true);
                    }
                    break;
                case APP:
                    ca = (Content_AmoebaSequencer) msg.content;
                    msgs.add(msg.logicalClock, ca.getContentMsg() );
                    
                    //System.out.println("p"+ ID+ " received "+msg.logicalClock + " of p"+msg.sender+"("+msg.physicalClock+" at "+clock+")");
                    ca.ACKS.clean();
                    ACKS.add(msg.logicalClock);
                    ca.ACKS.add(ACKS);
                    //System.out.println("p"+msg.sender+" STATE ACKS = "+ca.ACKS);
                    if (clock - SENT >= ts) {
                        this.createMessage(clock, ID, Leader, ACK, ca, msg.logicalClock);
                        ACKS.clean();              
                        SENT = clock;
                        //System.out.println("p"+msg.sender+" FLUSHED ACKS = "+ACKS.size());
                    }
                    /* VER EMBEDDED DELIVERIES */
                    
                    break;
                case ACK:
                    if (AmILeader) {
                        ca = (Content_AmoebaSequencer) msg.content;
                        this.checkingQuorum(msg, ca);
                    }
                    
                    break;
                case DLV:
                    ca = (Content_AmoebaSequencer) msg.content;
                    System.out.println("p"+ID+" DELIVERING MSG = "+msg.logicalClock);
                    System.out.println("p"+ID+" DELIVERING = "+ca.DLVS);
                    
                    for (int i=0;i<ca.DLVS.size();i++) {
                            int numSeq = ca.DLVS.toVector()[i];
                            System.out.println("p"+ID+" delivering m"+numSeq);
                            Message m = (Message) msgs.getMsgs(numSeq).get(0);
                            infra.app_in.add(clock, m);
                            Simulator.reporter.stats("blocking time", clock - m.receptionTime);
                        }
                    break;
            }
            
        }
        
}