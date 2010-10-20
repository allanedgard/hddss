package br.ufba.lasid.hddss;


import br.ufba.lasid.hddss.RuntimeContainer;
import org.apache.commons.math.stat.descriptive.*;

public class Simulator  extends Thread
{
    java.io.PrintStream out;
    char modo;
    int clock;
    int m;
    int n;
    int tempofinal;
    Agent p[];
    Network network;
    DescriptiveStatistics atraso_recepcao;
    DescriptiveStatistics tempo_transmissao;
    DescriptiveStatistics atraso_entrega;
    DescriptiveStatistics atraso_fila;

    double DESVIO;
    boolean debug_mode;
    boolean fim;
    char tipo;
    int charge;
    public static double ro = .001;
    public static int maxro = 5;
    public static Configurations config;
    
    public final int obtemAtraso(int i, int j)
    {
                return (int) (network.Channels[i][j].delay());
         
    }
     
    public final synchronized void ok() {
        m++;
        if (m==n) {
            fim = true;
            try {
                this.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }                 

    }

    public final synchronized void iniciaModoHwClock() {
        try {
            out.println("modo emulação de relógio");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i=0;i<n;i++) {
            p[i].startup();
        }
        
        boolean done = false;

        while(!done){

            done = true;

            for(int i = 0; i < n; i++){

                p[i].infra.avancaTick();

                /* 
                  if simulation time isn't over and p is not crashed then 
                  it hasn't been done yet.
                 */

                done = done && ((p[i].infra.clock.value() >= this.tempofinal) || !p[i].status());
            }

            network.avancaTick();
        }

        network.done = true;
    } 
    
    public final synchronized void inicia() {
        try {
            out.println("modo thread");
        } catch (Exception e) {
            e.printStackTrace();
        }

        network.start();
        
        for(int i = 0; i < n; i++)
        {
            p[i].start();
        }
    
                

    } 
    
    public final synchronized void finaliza() {
        for(int i = 0; i < n; i++)
        {
            p[i].stop();
        }

        network.stop();
    } 
    
    public final boolean sincTicks(int j) {
        double clockreal[] = new double[n];
        double menor;
        menor = Double.MAX_VALUE;
        for (int i = 0;i<n;i++ ){
            clockreal[i] = p[i].infra.clock.value() + ro * p[i].infra.clock.tickValue();
            if (clockreal[i] < menor) menor = clockreal[i];
        }
        for (int i = 0;i<n;i++ )
            if ( (p[i].tipo == 's') && p[i].status() ) 
                if (Math.abs(clockreal[i] - clockreal[j]) > DESVIO ) {
                    if (clockreal[j] == menor) {
                            return true;            
                        }
                    else return false;
            }
        return true;
    }
    
    public final double calculaDiferenca(int i, int j) {
        double clockr_i, clockr_j;
        clockr_i = p[i].infra.clock.value() + ro * p[i].infra.clock.tickValue();
        clockr_j = p[j].infra.clock.value() + ro * p[j].infra.clock.tickValue();
        return Math.abs(clockr_i - clockr_j);
    }
    
    private final synchronized void verificaPausa() throws InterruptedException {
        while (!fim) {
            wait();
        }
    }
    
    public final void run()
    {
        m = 0;
        p = new Agent[n];
        
        init();
        
        if (modo == 't') {
        
        inicia();
        
        try {
        verificaPausa();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        finaliza();
        
        }
        
        else 
            
        iniciaModoHwClock();

        estatisticas();
    }

    public void estatisticas() {
        System.out.println("execucao terminada:");
        java.util.Date data = new java.util.Date();
        System.out.println(data.toString());
        for (int i = 0; i< 256; i++){
            if (network.unicasts[i] != 0) {
                System.out.println("total de unicast classe "+i+" = "+network.unicasts[i]);
            }
            if (network.broadcasts[i] != 0) {
                System.out.println("total de broadcast classe "+i+" = "+network.broadcasts[i]);
            }
        }
        System.out.println("media atraso fim-a-fim = "+this.atraso_entrega.getMean()+", std dev atraso fim-a-fim = "+atraso_entrega.getStandardDeviation()  
                           +", maximo atraso fim-a-fim = "+this.atraso_entrega.getMax()+", min atraso fim-a-fim = "+atraso_entrega.getMin() 
                           );
        System.out.println("media atraso recepcao-entrega = "+this.atraso_recepcao.getMean()+", std dev atraso recepcao-entrega = "+atraso_recepcao.getStandardDeviation()
                           +", maximo atraso recepcao-entrega = "+this.atraso_recepcao.getMax()+", min atraso recepcao-entrega = "+atraso_recepcao.getMin() 
                           );  
        System.out.println("media atraso envio-recepcao = "+this.tempo_transmissao.getMean()+", std dev atraso envio-recepcao = "+tempo_transmissao.getStandardDeviation()
                           +", maximo atraso envio-recepcao = "+this.tempo_transmissao.getMax()+", min atraso envio-recepcao = "+tempo_transmissao.getMin() 
                           );
        System.out.println("media atraso fila= "+this.atraso_fila.getMean()+", std dev atraso fila = "+atraso_fila.getStandardDeviation()
                           +", maximo atraso fila = "+this.atraso_fila.getMax()+", min atraso fila = "+atraso_fila.getMin()
                           );

        System.out.println("total: "+this.tempo_transmissao.getN());
        out.close();
    }


    /*
     * O método connect() e o
     * método main podem ser sobre-carregados
     * para se implementar protocolos de agentes 
     * específicos
     */ 
    
    public final synchronized void avanco(RuntimeContainer rc) {
        rc.avancaTick();
    }

    public void init() {
        /*
         * Neste ponto se inicia os agentes e
         * os meios de comunicacao entre estes
         */

        try {
            Factory.config = config;

            //initing and setuping the network object            
            network = (Network) Factory.create(Network.TAG, Network.class.getName());
            network.init(this);
            Factory.setup(network, Network.TAG);
            
            //initing and setuping the agents
            for(int i = 0; i < n; i++){
                String TAG = Agent.TAG;
                String TAGi = TAG + "["+i+"]";
                p[i] = (Agent) Factory.create(Agent.TAG, Agent.class.getName());
                p[i].setId(i);
                p[i].setType(tipo);

                prepareAgent(p[i]);

                Factory.setup(p[i], TAG);
                
                if(!config.getString(TAGi, "null").equals("null")){
                    Factory.setup(p[i], TAGi);
                }
            }

            //initing and setuping the channels
            for(int i = 0; i < n; i++)
                for(int j = 0; j < n; j++)
                    network.handshaking(i, j);
            

    	} catch (Exception e) {
    		e.printStackTrace();
    	}

        //System.nic_out.println("Sistema inicializado!");
        
    }

    public void prepareAgent(Agent a) throws Exception{
        a.infra = new RuntimeContainer(this);
        a.infra.register(a);
        a.infra.nprocess = n;
        
        Clock _clock = (Clock) Factory.create(Clock.TAG, Clock.class.getName());
        Factory.setup(_clock, Clock.TAG);

        a.infra.clock = _clock;
        if(a.infra.clock instanceof Clock_Virtual){
            ((Clock_Virtual)(a.infra.clock)).nticks = (int) (1/ro);
            ((Clock_Virtual)(a.infra.clock)).rho =
                    ((new Randomize()).irandom(-maxro,maxro));
        }

        a.init();
    }

    public static Configurations getConfig(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: program_name configuration_file");
            System.exit(2);
        }

        ExtendedProperties ep;
        try {
            ep = new ExtendedProperties(args[0]);
        } catch (java.io.IOException ex) {
            throw new RuntimeException("Configuration file " + args[0]
                                       + " unreadable!");
        }
        return new Configurations(ep);
    }


    public static void main(String args[])
    {
        
        Configurations configGeral = getConfig(args);
        String WorkDir = configGeral.getString("WorkDir", ".");
        System.out.println("configurando");
        System.out.println(WorkDir);
        String[] classNames =
            configGeral.getStringArray("cenarios");
        if (classNames == null) {
            classNames = new String[0];
        }
        for (int i = 0; i < classNames.length; i++) {
            java.util.Date data = new java.util.Date();
            System.out.println(data.toString());
            System.out.println("cenario simulado: "+WorkDir+classNames[i]);
            config = getConfig(new String[] {WorkDir+classNames[i]});
            Simulator simulador = new Simulator(WorkDir+classNames[i]);
            simulador.run();
        }

        /*
        config = getConfig(args);
        Simulator simulador = new Simulator();
        simulador.run();*/
    }

    Simulator(String filename)
    {
        atraso_recepcao =  new DescriptiveStatistics();
        atraso_entrega = new DescriptiveStatistics();
        tempo_transmissao = new DescriptiveStatistics();
        atraso_fila = new DescriptiveStatistics();
        clock = 0;
        fim = false;
        debug_mode = false;
        try {
            out = new java.io.PrintStream(new java.io.FileOutputStream(filename+".saida.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tipo = config.getString("Tipo", "s").charAt(0);
        modo = config.getString("Modo", "t").charAt(0);
        debug_mode = config.getBoolean("Debug", debug_mode);
        tempofinal = config.getInteger("TempoFinal");
        n = config.getInteger("NumeroAgentes");
        DESVIO = config.getInteger("DesvioMaximo", 2);
    }

}