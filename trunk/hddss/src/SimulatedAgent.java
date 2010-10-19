/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class SimulatedAgent extends Agent{
    
    public final void criamensagem(int c, int remetente, int destinatario, int tipo, Object conteudo, int lc) {
        Message msg = new Message(remetente, destinatario, tipo, lc, c, conteudo);
        if (infra.faultModel == null) {
          infra.nic_out.adiciona(c, msg);
        }
        else infra.faultModel.enviaMensagem(c,msg);
    }

    public final void criamensagem(int c, int remetente, int destinatario, int tipo, Object conteudo, int lc, boolean pay) {
        Message msg = new Message(remetente, destinatario, tipo, lc, c, conteudo);
        msg.payload = pay;
        if (infra.faultModel == null) {
            infra.nic_out.adiciona(c, msg);
        }
        else infra.faultModel.enviaMensagem(c,msg);
    }


    public final void relaymensagem(int c, Message msg, int to) {
        msg.relayFrom = id;
        msg.relayTo = to;
        if (infra.faultModel == null) {
            infra.nic_out.adiciona(c, msg);
        }
        else infra.faultModel.enviaMensagem(c,msg);
    }

}
