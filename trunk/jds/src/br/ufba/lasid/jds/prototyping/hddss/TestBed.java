package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.prototyping.hddss.report.Reporter;
import org.apache.commons.math.stat.descriptive.*;

/**
 * This is the main class of HDDSS, allowing to run a set of simulated
 * scenarios described in the configuration files
 * @author allan
 */
public class TestBed  extends Thread implements RuntimeSupport
{
    String IP[], PORT[];
    
    java.io.PrintStream out;
    
    int clock;
    int m;

    public Agent p[];
    
    Network network;
    CPU cpu;

    boolean fim;
    //char tipo;
    int charge;
    public static double ro = .001;
    public static int maxro = 5;
    public Configurations config;
    boolean formattedReport = true;
    
    RuntimeVariables variables = new RuntimeVariables();
    
    public static final Reporter reporter = new Reporter();
   
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

    
    public boolean advance() {
        return true;
        };
   
    public final void inicia() {

        synchronized(this){

            int n = get(Variable.NumberOfAgents).<Integer>value();

            try {
                out.println("modo thread");
            } catch (Exception e) {
                e.printStackTrace();
            }
            network.start();

            //scheduler.start();

           int i = get("ID").<Integer>value();
           p[i].start();
           
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

            network.stop();

        }
    } 
    
    private final void verificaPausa() throws InterruptedException {
        synchronized(this){
            while (!fim) {
                wait();
            }
        }
    }
    
    @Override
    public final void run()
    {
        int n = get(Variable.NumberOfAgents).<Integer>value();
        m = 0;
        p = new Agent[n];
        
        init();
           
        inicia();
        
        try {
        verificaPausa();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        finaliza();
        
        estatisticas();
        
    }

    public void estatisticas() {
        System.out.println("simulation finished: ");
        java.util.Date data = new java.util.Date();        
        System.out.println(data.toString());
        if(formattedReport){
           reporter.report2FormattedTable(System.out);
        }else{
            reporter.report2UnformattedTable(System.out);
        }
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
            //network.init(this);
            Factory.setup(network, Network.TAG);

            set(Variable.Network, network);

            //prepareAgent(scheduler);
            
            //initing and setuping the agents
            int i = get("ID").<Integer>value();
            
             // CONFIGURAR A REDE OBTENDO OS ENDERECOS DE CADA AGENTE PARA
            // COMUNICACAO
            n = get(Variable.NumberOfAgents).<Integer>value();
            System.out.println("n="+n);
            IP = new String[n];
            PORT = new String[n];
            for(int j = 0; j < n; j++){
                String TAGInfra = "IPort";
                String TAGInfrai = TAGInfra + "["+j+"]";
                System.out.println(j);
                if(!config.getString(TAGInfrai, "null").equals("null")){
                    String ip = config.getString(TAGInfrai);
                    System.out.println(ip);

                    if (ip.indexOf(":") > -1) {
                        IP[j]= ip.substring(0, ip.indexOf(":")-1);
                        PORT[j] = ip.substring(ip.indexOf(":")+1);
                        System.out.println("ip = "+IP[j]);
                        System.out.println("port = "+PORT[j]);
                    }
                        
                }else{
                    System.out.println(config.getString(TAGInfra));
                }                      
            }
            
            System.out.println("Agent "+i);
            String TAG = Agent.TAG;
            String TAGi = TAG + "["+i+"]";
                
            if(!config.getString(TAGi, "null").equals("null")){
                    p[i] = (Agent) Factory.create(TAGi, Agent.class.getName());
            }else{
                    p[i] = (Agent) Factory.create(Agent.TAG, Agent.class.getName());
            }

            prepareAgent(p[i]);

            Factory.setup(p[i], TAG);
                
            if(!config.getString(TAGi, "null").equals("null")){
                Factory.setup(p[i], TAGi);
            }
           
             
            /* 
             * CONFIGURAR UM INFRA QUE MAPEIA OS ENDERECOS E POSSA REENVIAR
             * 
             */
            
            System.out.println("fim");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

        
    }

    public void prepareAgent(Agent a) throws Exception{
        int n = get(Variable.NumberOfAgents).<Integer>value();
        
        /* REVISAR ESTA PARTE */
        a.infra = (RuntimeContainer) new br.ufba.lasid.jds.prototyping.hddss.MiddlewareRuntimeContainer(this);
        a.infra.register(a);
        a.infra.nprocess = n;
        ((MiddlewareRuntimeContainer) a.infra).IP = IP;
        ((MiddlewareRuntimeContainer) a.infra).PORT = PORT;        
        
        /* REVISAR ESTA PARTE */
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

    public static void main(String args[])
    {
        
    }

    TestBed(Configurations conf, String filename, int id)
    {
        clock = 0;
        fim = false;
        config = conf;

        try {
            out = new java.io.PrintStream(new java.io.FileOutputStream(filename+".out"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        set("ID", id);
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
        set(Variable.FileName, filename);

        formattedReport =  config.getBoolean("FormattedReport", true);       
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