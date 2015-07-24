package br.ufba.lasid.jds.prototyping.hddss;

public class FaultModelAgent_Omission extends FaultModelAgent {
   
   FaultModelAgent_Omission(RuntimeContainer a) {
        super(a);
        infra.nic_in = new Buffer_Omission();
        infra.nic_out = new Buffer_Omission();
   }
   
   FaultModelAgent_Omission(RuntimeContainer a, double prob) {
        super(a);
        infra.nic_in = new Buffer_Omission(prob);
        infra.nic_out = new Buffer_Omission(prob);
   }

    @Override
   public void increaseTick() {
       infra.execute();
   }

    @Override
   public boolean status() {
       return true;
   }

}
