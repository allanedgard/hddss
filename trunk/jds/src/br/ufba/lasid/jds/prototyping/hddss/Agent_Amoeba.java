/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

/**
 *
 * @author Anne
 */
public class Agent_Amoeba extends SimulatedAgent{
    int lider;
    int numSequencia;
    int logicalClock;
    Randomize r;
    double prob;
    double miss;
    int DELTA;
    int interval;//intervalo minimo "ocioso" para envio de um ACK
    int timeLastReq; //tempo em q enviou a última requisição
    final int BROAD_REQ = 1; //requisição de broadcast
    final int APP = 2;
    final int MISSING = 3; //requisição de mensagens perdidas
    final int ACK = 4; //atualizar
    Hashtable msg_recebidas;
    Buffer history;
    Hashtable memberInfo;

    Agent_Amoeba(){
        super();
    }

    public void setProbabilidadeGerarPacote (String po)
    {
        prob = Double.parseDouble(po);
    }

    public void setProbabilidadePerderPacote (String m)
    {
        miss = Double.parseDouble(m);
    }

    public void setDelta(String dt)
    {
        DELTA = Integer.parseInt(dt);
    }

    public int getDelta() {
        return DELTA;
    }
        // return super.getDeltaMax();
    
    public void setInterval(String in)
    {
        interval = Integer.parseInt(in);
    }

    public void setup()
    {
        lider = 0;
        numSequencia = -1;
        logicalClock = 0;
        r = new Randomize();
        msg_recebidas = new Hashtable();
        history = new Buffer();
        memberInfo = new Hashtable();
    }

    public void startup(){}

    public void execute()
    {
        int clock = (int)infra.clock.value();

        if(r.uniform() <= prob)
        {
            this.createMessage(clock, ID, lider, BROAD_REQ, new Content_Amoeba(logicalClock), -1);
            timeLastReq = clock;
        }

        if((clock - timeLastReq) >= interval)
        {
            this.createMessage(clock, ID, lider, ACK, new Content_Amoeba(logicalClock), -1);
        }

        cleanHistory();
    }

    public void receive(Message m)
    {
        Integer DESVIO = infra.context.get(RuntimeSupport.Variable.MaxDeviation).value();
        int clock = (int)infra.clock.value();
        switch(m.type)
        {
            case BROAD_REQ:
                if(isLider())
                {
                    numSequencia++;
                    history.add(numSequencia, m);
                    this.createMessage(clock, m.sender, infra.nprocess, APP, m.content, numSequencia, true);
                }
                break;

            case APP:
                int time = m.physicalClock + (int) DESVIO + getDelta();
                memberInfo.put(m.sender, ((Content_Amoeba)m.content).getLast());
                if(m.logicalClock > logicalClock)
                {
                    if(r.uniform() <= miss)
                        this.createMessage(clock, ID, lider, MISSING, new Content_Amoeba(logicalClock, m.logicalClock), -1);
                }
                infra.app_in.add(time, m);
                break;

            case MISSING:
                int[] mensagens = ((Content_Amoeba)m.content).getMissing();
                Message msg;
                if(isLider() && (mensagens.length!=0))
                {
                    for(int i=0; i<mensagens.length; i++)
                    {
                        msg = (Message)((ArrayList)history.inside.get(mensagens[i])).get(0);
                        this.createMessage(clock, msg.sender, m.sender, APP, msg.content, mensagens[i], true);
                    }
                }
                break;

            case ACK:
                memberInfo.put(m.sender, ((Content_Amoeba)m.content).getLast());
                break;
        }
    }

    public void deliver(Message m)
    {
        if(logicalClock == m.logicalClock)
        {
            if(!isLider())
                history.add(logicalClock, m);
            logicalClock++;
        }
        else if(logicalClock < m.logicalClock)
        {
            msg_recebidas.put(m.logicalClock, m);
        }

        //Se já tiver recebido e tá em msg_recebidas
        while(msg_recebidas.containsKey(logicalClock))
        {
            Message msg = (Message)msg_recebidas.remove(logicalClock);
            
            Simulator.reporter.stats("blocking time", (int)infra.clock.value()-msg.receptionTime);
            if(!isLider())
                history.add(logicalClock, m);
            logicalClock++;
        }
    }

    public boolean isLider()
    {
        return (lider == ID);
    }

    public void cleanHistory()
    {
        if(!memberInfo.isEmpty() && !history.inside.isEmpty())
        {
            int menor = getLowestValue(memberInfo);

            for(int i=0; i<=menor; i++)
            {
                history.getMsgs(i);
            }
        }
    }
    
    public int getLowestValue(Hashtable h)
    {
        ArrayList a = Collections.list(h.elements());
        Collections.sort(a);

        return Integer.parseInt(a.get(0).toString());
    }

}
