package br.ufba.lasid.jds.prototyping.hddss;

public abstract class FaultModelAgent {
   RuntimeContainer infra;
   static final String TAG = "ftmodel";
   FaultModelAgent(RuntimeContainer a) {
        infra = a;
        infra.nic_in = new Buffer();
        infra.nic_out = new Buffer();
   }

   FaultModelAgent() {
   }

   void initialize(RuntimeContainer a) {
        infra = a;
        infra.nic_out = new Buffer();
        infra.nic_in = new Buffer();

   }

   public void increaseTick(){
       infra.execute();
   };

   public boolean status() {
        return true;
   }

   public void sendMessage(long c, Message msg) {
       infra.nic_out.add(c, msg);
   }

}
