package br.ufba.lasid.hddss;
import br.ufba.lasid.hddss.FaultModelAgent;
import br.ufba.lasid.hddss.*;

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

   public void increaseTick() {
       infra.execute();
   }

   public boolean status() {
       return true;
   }

}
