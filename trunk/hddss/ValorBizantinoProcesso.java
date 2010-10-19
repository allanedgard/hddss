/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class ValorBizantinoProcesso extends ProcessFaultModel {

    Randomico r;
    double prob;
    
    ValorBizantinoProcesso(RuntimeContainer a, double p) {
        super(a);
        r = new Randomico();
        prob = p;
    }
    
    ValorBizantinoProcesso(RuntimeContainer a) {
        super(a);
        r = new Randomico();
        prob = r.uniform();
    }   

    public void enviaMensagem(int c, Mensagem msg) {
        if (r.uniform() <= prob) {
                    super.enviaMensagem(c, msg);
                }
        else {
            msg = new Mensagem(msg.remetente, msg.destinatario, (int) r.expntl((double) msg.tipo), (int) r.expntl((double) msg.relogioLogico), msg.relogioFisico, msg.conteudo);
            super.enviaMensagem(c, msg);
            
        }
    }    
    
}
