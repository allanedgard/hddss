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
        
        /*
         *  CLASSES OF MESSAGES
         * 
         */
        final int APP = 12;
        final int ACK = 13;
        
        int [] BM;
        
        
        int TOTAL;
        long timeout;
            
        int cont;
        int sequencia;

            
        Randomize r;
         
        
        public Agent_protocoloA() {
            super();
        }
    
        @Override
        public void setup() {
            finalTime = getInfra().context.get(RuntimeSupport.Variable.FinalTime).<Integer>value();

            Leader = 1;
            if (getAgentID()==Leader)
                AmILeader = true;
            
            r = new Randomize();
            
            BM = new int[getInfra().nprocess];
            for (int i=0;i<getInfra().nprocess;i++)
                BM[i]=-1;
            
            TOTAL = 1000000;
            long timeout = Long.MAX_VALUE;
            cont =0;
            sequencia = 0;
        }
        
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
            if (AmILeader)
                if ( ((cont==getInfra().getNumberOfProcess()) || (getInfra().clock.value()  - timeout > DELTA) ) && (sequencia<TOTAL)) {
                    //System.out.println("Enviando em "+getInfra().clock.value());
                    sequencia++;
                    sendGroupMsg(APP, "stuff", sequencia, true);
                    timeout = getInfra().clock.value() + DELTA;
                    cont=0;
            }
        }
        
        public void sendGroupMsg(int tipo, Object valor, int LC) {
            for (int j=0; j<getInfra().nprocess;j++) {
                this.createMessage(getInfra().clock.value(), this.getAgentID(), j, tipo, valor, LC);  }
        }
        
        public void sendGroupMsg(int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<getInfra().nprocess;j++) {
                this.createMessage(getInfra().clock.value(), this.getAgentID(), j, tipo, valor, LC, pay);
            }
        }

        public void relayGroupMsg(int i, int tipo, Object valor, int LC, boolean pay) {
            for (int j=0; j<getInfra().nprocess;j++) {
                this.createMessage(getInfra().clock.value(), i, j, tipo, valor, LC, pay);
            }
        }        
                
        @Override
        public void receive(Message msg) {
            super.receive(msg);
            Content_AmoebaSequencer ca;
            int clock = (int)getInfra().clock.value();

            switch (msg.type) {
                case APP:
                    /*
                    System.out.println("Recebendo APP em "+clock);
                    System.out.println("enviada em = "+msg.physicalClock);
                    System.out.println("seq = "+msg.logicalClock);
                    System.out.println("id = "+msg.sender);
                    System.out.println("myid = "+getAgentID());
                    */
                    this.createMessage(clock, getAgentID(), Leader, ACK, "stuff", msg.logicalClock);
                    break;
                case ACK:
                    if (AmILeader) {
                        /*
                        System.out.println("Recebendo ACK em "+clock);
                        System.out.println("enviada em = "+msg.physicalClock);
                        System.out.println("seq = "+msg.logicalClock);
                        System.out.println("id = "+msg.sender);
                        System.out.println("myid = "+getAgentID());
                        */
                        if (sequencia > BM[msg.sender]) {
                            BM[msg.sender] =  sequencia;
                            cont++;
                        }
                        deliver(msg);
                    }
                    break;
            }
            
        }
        
}