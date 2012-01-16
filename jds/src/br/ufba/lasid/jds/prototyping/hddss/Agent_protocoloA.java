package br.ufba.lasid.jds.prototyping.hddss;

public class Agent_protocoloA extends SimulatedAgent {

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
        final int APP = 12;
        final int ACK = 13;
        
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
        
        int TOTAL;
        int timeout;
            
        int cont;
        int sequencia;
                
        boolean bloquearEntrega;
        boolean consenso;

            
        Randomize r;
         
        
        Agent_protocoloA() {
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
            
            count = 0;
            
            TOTAL = 1000000;
            timeout = Integer.MAX_VALUE;
            
            cont =0;
            sequencia = 0;
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
        /*
       while there are packets to be transmitted
            send packet of sequence seq
            while there are acks ready to be received
                receive ack
                add ack to ack list if not already there
                if ackList is complete
                    increment seq
                    clear ackList
                else
                    wait on E[Xt], the timeout handling time
        */
        if ( ((cont==infra.getNumberOfProcess()) || (timeout-clock > DELTA) ) && (sequencia<TOTAL)) {
            Content_AmoebaSequencer ca = new Content_AmoebaSequencer(LastACK, "stuff");
            sequencia++;
            sendGroupMsg(clock, APP, ca, sequencia, true);
            timeout = clock + DELTA;
            cont=0;
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
                
        @Override
        public void receive(Message msg) {
            
            Content_AmoebaSequencer ca;
            int clock = (int)infra.clock.value();

            switch (msg.type) {
                case APP:
                    ca = (Content_AmoebaSequencer) msg.content;
                    this.createMessage(clock, ID, Leader, ACK, ca, msg.logicalClock);
                    break;
                case ACK:
                    if (AmILeader) {
                        ca = (Content_AmoebaSequencer) msg.content;
                        if (msg.logicalClock == sequencia) 
                            cont++;
                    }
                    break;
            }
            
        }
        
}