package br.ufba.lasid.jds.prototyping.hddss.instances;

import br.ufba.lasid.jds.prototyping.hddss.Buffer;
import br.ufba.lasid.jds.prototyping.hddss.Message;
import br.ufba.lasid.jds.prototyping.hddss.Randomize;
import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport;
import br.ufba.lasid.jds.prototyping.hddss.SimulatedAgent;

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
         
        
        public Agent_protocoloA() {
            super();
        }
    
        @Override
        public void setup() {
            finalTime = getInfra().context.get(RuntimeSupport.Variable.FinalTime).<Integer>value();

            bloquearEntrega = false;
  
            Leader = 1;
            if (getAgentID()==Leader)
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
            
            BM = new int[getInfra().nprocess];
            for (int i=0;i<getInfra().nprocess;i++)
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
               int clock = (int)getInfra().clock.value();
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
        if ( ((cont==getInfra().getNumberOfProcess()) || (timeout-clock > DELTA) ) && (sequencia<TOTAL)) {
            System.out.println("Enviando em "+clock);
            Content_AmoebaSequencer ca = new Content_AmoebaSequencer(LastACK, "stuff");
            sequencia++;
            sendGroupMsg(clock, APP, ca, sequencia, true);
            timeout = clock + DELTA;
            cont=0;
        }
        }
        
        public void sendGroupMsg(int clock, int tipo, Object valor, int LC) {
            for (int j=0; j<getInfra().nprocess;j++) {
                this.createMessage(clock, this.getAgentID(), j, tipo, valor, LC);  }
        }
        
        public void sendGroupMsg(int clock, int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<getInfra().nprocess;j++) {
                this.createMessage(clock, this.getAgentID(), j, tipo, valor, LC, pay);
            }
        }

        public void relayGroupMsg(int clock, int i, int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<getInfra().nprocess;j++) {
                this.createMessage(clock, i, j, tipo, valor, LC, pay);
            }
        }        
                
        @Override
        public void receive(Message msg) {
            super.receive(msg);
            Content_AmoebaSequencer ca;
            int clock = (int)getInfra().clock.value();

            switch (msg.type) {
                case APP:
                    System.out.println("Recebendo APP em "+clock);
                    System.out.println("enviada em = "+msg.physicalClock);
                    System.out.println("seq = "+msg.logicalClock);
                    System.out.println("id = "+msg.sender);
                    System.out.println("myid = "+getAgentID());
                    ca = (Content_AmoebaSequencer) msg.content;
                    this.createMessage(clock, getAgentID(), Leader, ACK, ca, msg.logicalClock);
                    break;
                case ACK:
                    if (AmILeader) {
                    System.out.println("Recebendo ACK em "+clock);
                    System.out.println("enviada em = "+msg.physicalClock);
                    System.out.println("seq = "+msg.logicalClock);
                    System.out.println("id = "+msg.sender);
                    System.out.println("myid = "+getAgentID());
                        ca = (Content_AmoebaSequencer) msg.content;
                        if (msg.logicalClock == sequencia) 
                            cont++;
                        deliver(msg);
                    }
                    break;
            }
            
        }
        
}