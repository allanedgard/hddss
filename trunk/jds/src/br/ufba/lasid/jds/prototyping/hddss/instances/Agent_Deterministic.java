package br.ufba.lasid.jds.prototyping.hddss.instances;

import br.ufba.lasid.jds.prototyping.hddss.Buffer;
import br.ufba.lasid.jds.prototyping.hddss.Message;
import br.ufba.lasid.jds.prototyping.hddss.Randomize;
import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport;
import br.ufba.lasid.jds.prototyping.hddss.SimulatedAgent;

public class Agent_Deterministic extends SimulatedAgent {

        int Leader;         // Who is the Leader? Process Number
        boolean AmILeader;  // Am I the Leader? YES or NO
        int Sequential;     // Sequential Number        
        int finalTime;       // Final Simulation Time
               
        
        int LogicalClock;
        int LastTimeSent;
        
        /*
         *  CLASSES OF MESSAGES
         * 
         */
        final int APP = 12;
        final int ACK = 13;
        
        int cont;

            
        Randomize r;
         
        
        public Agent_Deterministic() {
            super();
        }
    
        @Override
        public void setup() {
            finalTime = getInfra().context.get(RuntimeSupport.Variable.FinalTime).<Integer>value();
        }
        
        /*
         *  Parameters
         */
               
    @Override
        public void startup(){

        }   
     
        
    @Override
        public void execute() {
               int clock = (int)getInfra().clock.value();
               if (clock % 100 == 0) {
                   // System.out.println("envio clock = "+clock);
                   Content_AmoebaSequencer ca = new Content_AmoebaSequencer(0, "stuff");
                   sendGroupMsg(clock, APP, ca, cont++, true);
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
                    /*
                    System.out.println("Recebendo APP em "+clock);
                    System.out.println("enviada em = "+msg.physicalClock);
                    System.out.println("seq = "+msg.logicalClock);
                    System.out.println("id = "+msg.sender);
                    System.out.println("myid = "+getAgentID());
                    */
                    break;
            }
            
        }
        
}