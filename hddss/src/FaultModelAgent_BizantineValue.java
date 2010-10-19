/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class FaultModelAgent_BizantineValue extends FaultModelAgent {

    Randomize r;
    double prob;
    
    FaultModelAgent_BizantineValue(RuntimeContainer a, double p) {
        super(a);
        r = new Randomize();
        prob = p;
    }
    
    FaultModelAgent_BizantineValue(RuntimeContainer a) {
        super(a);
        r = new Randomize();
        prob = r.uniform();
    }   

    public void enviaMensagem(int c, Message msg) {
        if (r.uniform() <= prob) {
                    super.enviaMensagem(c, msg);
                }
        else {
            msg = new Message(msg.remetente, msg.destinatario, (int) r.expntl((double) msg.tipo), (int) r.expntl((double) msg.relogioLogico), msg.relogioFisico, msg.conteudo);
            super.enviaMensagem(c, msg);
            
        }
    }    
    
}
