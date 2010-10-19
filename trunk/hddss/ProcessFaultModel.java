
/**
 *
 * @author Administrador
 */
public abstract class ProcessFaultModel {
   RuntimeContainer infra;
   static final String TAG = "ftmodel";
   ProcessFaultModel(RuntimeContainer a) {
        infra = a;
        infra.nic_in = new Buffer();
        infra.nic_out = new Buffer();
   }

   ProcessFaultModel() {
   }

   void inicializa(RuntimeContainer a) {
        infra = a;
        infra.nic_out = new Buffer();
        infra.nic_in = new Buffer();

   }

   public void avancaTick(){
       infra.execute();
   };

   public boolean status() {
        return true;
   }

   public void enviaMensagem(int c, Mensagem msg) {
       infra.nic_out.adiciona(c, msg);
   }

}
