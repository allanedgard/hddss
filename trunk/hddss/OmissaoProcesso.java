public class OmissaoProcesso extends ProcessFaultModel {
   
   OmissaoProcesso(RuntimeContainer a) {
        super(a);
        infra.nic_in = new BufferOmissao();
        infra.nic_out = new BufferOmissao();
   }
   
   OmissaoProcesso(RuntimeContainer a, double prob) {
        super(a);
        infra.nic_in = new BufferOmissao(prob);
        infra.nic_out = new BufferOmissao(prob);
   }

   public void avancaTick() {
       infra.execute();
   }

   public boolean status() {
       return true;
   }

}
