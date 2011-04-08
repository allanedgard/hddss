package br.ufba.lasid.jds.prototyping.hddss;

import org.apache.commons.math.stat.descriptive.*;

/**
 * This is the main class of HDDSS, allowing to run a set of simulated
 * scenarios described in the configuration files
 * @author allan
 */
public class Simulator  extends Thread implements RuntimeSupport
{
    java.io.PrintStream out;
    //char modo;
    int clock;
        int m;
    //int n;
    //int tempofinal;
    public Agent p[];
    
    //SimulatedScheduler scheduler = new SimulatedScheduler();

    Network network;
    CPU cpu;
//    DescriptiveStatistics receptionDelay;
//    DescriptiveStatistics tempo_transmissao;
//    DescriptiveStatistics deliveryDelay;
//    DescriptiveStatistics atraso_fila;

    //double DESVIO;
    //boolean debug_mode;
    boolean fim;
    //char tipo;
    int charge;
    public static double ro = .001;
    public static int maxro = 5;
    public static Configurations config;
    
    RuntimeVariables variables = new RuntimeVariables();
    
    public final int obtemAtraso(int i, int j)
    {
                return (int) (network.Channels[i][j].delay());
         
    }

    public final int obtemAtraso(int i, IProcessable data){
       return (int) p[i].infra.cpu.exec(data);
    }
     
    public final void ok() {
        synchronized(this){
            m++;
            if (m==get(Variable.NumberOfAgents).<Integer>value().intValue()) {
                fim = true;
                try {
                    this.notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public final void iniciaModoHwClock() {
        synchronized(this){
            int n = get(Variable.NumberOfAgents).<Integer>value();
            int finalTime = get(Variable.FinalTime).<Integer>value();

            try {
                out.println("[*** Clock emulation mode ***]");
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i=0;i<n;i++) {
                p[i].startup();
                p[i].infra.cpu.start();
            }

            //scheduler.startup();

            boolean done = false;

            while(!done){

                done = true;

                for(int i = 0; i < n; i++){
                    synchronized(p[i].lock){
                        p[i].getInfra().increaseTick();
                    }
                    /*
                      if simulation time isn't over and p is not crashed then
                      it hasn't been done yet.
                     */

                    done = done && ((p[i].getInfra().clock.value() >= finalTime) || !p[i].status());
                }

                network.incTick();


                //scheduler.infra.increaseTick();

            }
            network.done = true;
        }
    } 
    
    public final void inicia() {

        synchronized(this){

            int n = get(Variable.NumberOfAgents).<Integer>value();

            try {
                out.println("modo thread");
            } catch (Exception e) {
                e.printStackTrace();
            }

            network.start();
            cpu.start();

            //scheduler.start();

            for(int i = 0; i < n; i++)
            {
                p[i].start();
            }  
        }
    } 
    
    public final void finaliza() {
        synchronized(this){
            int n = get(Variable.NumberOfAgents).<Integer>value();
            for(int i = 0; i < n; i++)
            {
                p[i].shutdown();
            }

            //scheduler.shutdown();

            cpu.stop();
            network.stop();

        }
    } 
    
    public final boolean sincTicks(int j) {
        
        double DESVIO = get(Variable.MaxDeviation).<Double>value();

        int n = get(Variable.NumberOfAgents).<Integer>value();
        double clockreal[] = new double[n];
        double menor;
        menor = Double.MAX_VALUE;
        for (int i = 0;i<n;i++ ){
            clockreal[i] = p[i].getInfra().clock.value() + ro * p[i].getInfra().clock.tickValue();
            if (clockreal[i] < menor) menor = clockreal[i];
        }
        for (int i = 0;i<n;i++ )
            if ( (p[i].getTipo() == 's') && p[i].status() )
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
        clockr_i = p[i].getInfra().clock.value() + ro * p[i].getInfra().clock.tickValue();
        clockr_j = p[j].getInfra().clock.value() + ro * p[j].getInfra().clock.tickValue();
        return Math.abs(clockr_i - clockr_j);
    }
    
    private final void verificaPausa() throws InterruptedException {
        synchronized(this){
            while (!fim) {
                wait();
            }
        }
    }
    
    public final void run()
    {
        int n = get(Variable.NumberOfAgents).<Integer>value();
        m = 0;
        p = new Agent[n];
        
        init();
        
        if (get(Variable.Mode).<String>value().equals("t")) {
        
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
        
        System.exit(0);
    }

    public void estatisticas() {
        System.out.println("simulation finished: ");
        java.util.Date data = new java.util.Date();
        System.out.println(data.toString());

        DescriptiveStatistics deliveryDelay = get(Variable.DlvDelayTrace).<DescriptiveStatistics>value();
        DescriptiveStatistics receptionDelay = get(Variable.RxDelayTrace).<DescriptiveStatistics>value();
        DescriptiveStatistics transmitionDelay = get(Variable.TxDelayTrace).<DescriptiveStatistics>value();
        DescriptiveStatistics queueDelay = get(Variable.QueueDelayTrace).<DescriptiveStatistics>value();
        
        for (int i = 0; i< 256; i++){
            if (network.unicasts[i] != 0) {
                System.out.println("total of unicast class "+i+" = "+network.unicasts[i]);
            }
            if (network.broadcasts[i] != 0) {
                System.out.println("total of broadcast class "+i+" = "+network.broadcasts[i]);
            }
            if (network.multicasts[i] != 0) {
                System.out.println("total of multicast class "+i+" = "+network.multicasts[i]);
            }
        }

//        for (int i = 0; i< 256; i++){
//            if (cpu.objects[i] != 0) {
//                System.out.println("total of " + cpu.objectsTAGs[i] + " objects = " + cpu.objects[i]);
//            }
//        }

        System.out.println("mean end-to-end delay = " + deliveryDelay.getMean() + ", end-to-end std dev delay = " + deliveryDelay.getStandardDeviation()
                           +", maximum end-to-end delay = " + deliveryDelay.getMax() + ", minimun end-to-end delay = " + deliveryDelay.getMin()
                           );
        System.out.println("mean reception-delivery delay = "+receptionDelay.getMean()+", repection-delivery std dev delay = "+receptionDelay.getStandardDeviation()
                           +", maximum repcetion-delivery delay = "+receptionDelay.getMax()+", minimum repcetion-delivery delay = "+receptionDelay.getMin()
                           );  
        System.out.println("mean send-reception delay = "+transmitionDelay.getMean()+", send-reception std dev delay = "+transmitionDelay.getStandardDeviation()
                           +", maximum send-reception delay = "+transmitionDelay.getMax()+", minimum send-reception = "+transmitionDelay.getMin()
                           );
        System.out.println("mean queue delay = "+queueDelay.getMean()+", queue std dev delay = "+queueDelay.getStandardDeviation()
                           +", maximum queue delay = "+queueDelay.getMax()+", minimum queue delay = "+queueDelay.getMin()
                           );

//        System.out.println("mean cpu delay = "+cpuDelay.getMean()+", cpu std dev delay = "+cpuDelay.getStandardDeviation()
//                           +", maximum cpu delay = "+cpuDelay.getMax()+", minimum cpu delay = "+cpuDelay.getMin()
//                           );

        System.out.println("total: " + transmitionDelay.getN());
        out.close();
    }


    /*
     * O método connect() e o
     * método main podem ser sobre-carregados
     * para se implementar protocolos de agentes 
     * específicos
     */ 
    
    public final void perform(RuntimeContainer rc) {
         synchronized(rc.agent.lock){
            rc.increaseTick();
        }
    }

    public void init() {
        /*
         * Neste ponto se inicia os agentes e
         * os meios de comunicacao entre estes
         */
        int n = get(Variable.NumberOfAgents).<Integer>value();
        try {
            Factory.config = config;

            //initing and setuping the network object            
            network = (Network) Factory.create(Network.TAG, Network.class.getName());
            network.init(this);
            Factory.setup(network, Network.TAG);

            set(Variable.Network, network);

            //prepareAgent(scheduler);
            
            //initing and setuping the agents
            for(int i = 0; i < n; i++){
                String TAG = Agent.TAG;
                String TAGi = TAG + "["+i+"]";

                if(!config.getString(TAGi, "null").equals("null")){
                    p[i] = (Agent) Factory.create(TAGi, Agent.class.getName());
                }else{
                    p[i] = (Agent) Factory.create(Agent.TAG, Agent.class.getName());
                }

                p[i].setAgentID(i);
                p[i].setType(get(Variable.Type).<String>value().charAt(0));

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
        int n = get(Variable.NumberOfAgents).<Integer>value();
        a.infra = new RuntimeContainer(this);
        a.infra.register(a);
        a.infra.nprocess = n;
        
        AbstractClock _clock = (AbstractClock) Factory.create(AbstractClock.TAG, AbstractClock.class.getName());
        Factory.setup(_clock, AbstractClock.TAG);

        a.infra.clock = _clock;
        if(a.infra.clock instanceof Clock_Virtual){
            ((Clock_Virtual)(a.infra.clock)).nticks = (int) (1/ro);
            ((Clock_Virtual)(a.infra.clock)).rho =
                    ((new Randomize()).irandom(-maxro,maxro));
        }

         a.infra.cpu = (CPU) Factory.create(CPU.TAG, CPU.class.getName());
         
         Factory.setup(a.infra.cpu, CPU.TAG);

         a.infra.cpu.setClock(_clock);

        //a.infra.scheduler = scheduler;

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
        clock = 0;
        fim = false;

        try {
            out = new java.io.PrintStream(new java.io.FileOutputStream(filename+".saida.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        set(Variable.RxDelayTrace,  new DescriptiveStatistics());
        set(Variable.TxDelayTrace,  new DescriptiveStatistics());
        set(Variable.DlvDelayTrace,  new DescriptiveStatistics());
        set(Variable.QueueDelayTrace,  new DescriptiveStatistics());
        set(Variable.Type,  config.getString("Type", "s").substring(0, 1));
        set(Variable.Mode,  config.getString("Mode", "t").substring(0, 1));
        set(Variable.Debug, config.getBoolean("Debug", false));
        set(Variable.MaxSimulationTime, config.getInteger("FinalTime"));
        set(Variable.FinalTime, config.getInteger("FinalTime"));
        set(Variable.NumberOfAgents, config.getInteger("NumberOfAgents"));
        set(Variable.MaxDeviation, config.getInteger("MaximumDeviation", 2));
        set(Variable.StdOutput, out);
        set(Variable.ClockDeviation, ro);
        set(Variable.MaxClockDeviation, maxro);
        //set(Variable.Scheduler, scheduler);
        
    }
    
    public Value get(Variable variable) {
        return variables.get(variable);
    }

    public <U> void set(Variable variable, U value){
        variables.set(variable, value);
    }
    public Value get(String name) {
        return variables.get(name);
    }

    public <U> void set(String name, U value) {
        variables.set(name, value);
    }

}