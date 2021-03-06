package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.prototyping.hddss.report.Reporter;
import org.apache.commons.math.stat.descriptive.*;

/**
 * This is the main class of HDDSS, allowing to run a set of simulated
 * scenarios described in the configuration files
 * @author allan
 */
public class Simulator  extends Thread implements RuntimeSupport
{
    java.io.PrintStream out;
    int clock;
    int m;
    /*  #scenarios
     *  public Agent p[];
     */
    
    String NAME;
    
    Scenario scenario;
    public Network network;
    static int numInstances =0;
    static int numFinishedInstances=0;
    boolean end;
    int charge;
    
    /*  COLOCAR NO ARQUIVO DE CONF ro E maxro */
    public static double ro = .1;
    public static int maxro = 5;
    
    public static Configurations config;
    boolean formattedReport = true;
    
    RuntimeVariables variables = new RuntimeVariables();
    
    //public static final Reporter reporter = new Reporter();

    public final int getDelay(int i, int j)
    {
                return (int) (network.channels[i][j].delay());
         
    }

    public final Network getNetwork() {
        return network;
    }
    
    public final int getDelay(int i, IProcessable data){
       /*   #scenarios
         *  return (int) p[i].infra.cpu.exec(data);
         */
        return (int) scenario.p[i].getInfra().cpu.exec(data);
    }
     
    public final void ok() {
        synchronized(this){
            m++;
            if (m==get(Variable.NumberOfAgents).<Integer>value().intValue()) {
                end = true;
                try {
                    this.notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public final void initiateSimulatedHwClockMode() {
        synchronized(this){
            int n = get(Variable.NumberOfAgents).<Integer>value();
            //int finalTime = get(Variable.FinalTime).<Integer>value();

            try {
                out.println("[*** Clock emulation mode ***]");
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i=0;i<n;i++) {
                scenario.p[i].startup();
                /*  #scenario
                *   p[i].startup();
                * 
                */
            }

            boolean done = false;

            while(!(done = advance()));
            
            network.setDone(true);
        }
    } 

    public boolean advance(){
      int n = get(Variable.NumberOfAgents).<Integer>value();
      int finalTime = get(Variable.FinalTime).<Integer>value();

      boolean done = true;

      for(int i = 0; i < n; i++){
         synchronized(scenario.p[i].lock){
            scenario.p[i].getInfra().increaseTick();
            /*  #scenario
             *  synchronized(p[i].lock){
             *      p[i].getInfra().increaseTick();
             */
         }
         /* if simulation time isn't over and p is not crashed,
          * then it hasn't been done yet. */
         done = done && ((scenario.p[i].getInfra().clock.value() >= finalTime) || !scenario.p[i].status());
         /*     #scenario
          *     done = done && ((p[i].getInfra().clock.value() >= finalTime) || !p[i].status());
          */
         
      }
      scenario.increaseTick();
      network.increaseTick();

      return done;
    }
    public final void initiate() {

        synchronized(this){

            int n = get(Variable.NumberOfAgents).<Integer>value();

            try {
                out.println("modo thread");
            } catch (Exception e) {
                e.printStackTrace();
            }
            network.start();

            //scheduler.start();

            for(int i = 0; i < n; i++)
            {
                scenario.p[i].start();
                /*  #scenario
                 *  p[i].start();
                 */
            }  
        }
    } 
    
    public final void terminate() {
        synchronized(this){
            int n = get(Variable.NumberOfAgents).<Integer>value();

            for(int i = 0; i < n; i++)
            {
                scenario.p[i].shutdown();
                /*  #scenario
                 *  p[i].shutdown();
                 */
            }

            //scheduler.shutdown();

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
            clockreal[i] = scenario.p[i].getInfra().clock.value() + ro * scenario.p[i].getInfra().clock.tickValue();
            /*  #scenario
             *  clockreal[i] = p[i].getInfra().clock.value() + ro * p[i].getInfra().clock.tickValue();
             */
            if (clockreal[i] < menor) menor = clockreal[i];
        }
        for (int i = 0;i<n;i++ )
            
            if ( (scenario.p[i].getTipo() == 's') && scenario.p[i].status() )
            /*  #scenario
             *  if ( (p[i].getTipo() == 's') && p[i].status() )
             */
                if (Math.abs(clockreal[i] - clockreal[j]) > DESVIO ) {
                    if (clockreal[j] == menor) {
                            return true;            
                        }
                    else return false;
            }
        return true;
    }
    
    public final double calcDiff(int i, int j) {
        double clockr_i, clockr_j;
        clockr_i = scenario.p[i].getInfra().clock.value() + ro * scenario.p[i].getInfra().clock.tickValue();
        clockr_j = scenario.p[j].getInfra().clock.value() + ro * scenario.p[j].getInfra().clock.tickValue();
        /*  #scenario
         *  clockr_i = p[i].getInfra().clock.value() + ro * p[i].getInfra().clock.tickValue();
         *  clockr_j = p[j].getInfra().clock.value() + ro * p[j].getInfra().clock.tickValue();
         * 
         */
        return Math.abs(clockr_i - clockr_j);
    }
    
    private final void verificaPausa() throws InterruptedException {
        synchronized(this){
            while (!end) {
                wait();
            }
        }
    }
    
    @Override
    public final void run()
    {
        java.util.Date data = new java.util.Date();
        System.out.println();
        System.out.println("Starting simulated scene: "+NAME+" at "+data.toString());
        int n = get(Variable.NumberOfAgents).<Integer>value();
        m = 0;
        /*  #scenario
         *  p = new Agent[n];
         */
        init();
        
        if (get(Variable.Mode).<String>value().equals("t")) {
        
        initiate();
        
        try {
        verificaPausa();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        terminate();
        
        }
        
        else 
            
        initiateSimulatedHwClockMode();

        statistics();
        
        // System.exit(0);
    }

    public synchronized void  statistics() {
        java.util.Date data = new java.util.Date();
        String text = "Results for simulated scene: "+NAME+"\n";
        text = text + "simulation finished: \n";
        text = text + data.toString() + "\n";
        if(formattedReport){
            scenario.reporter.report2FormattedTable(text, System.out);
        }else{
            scenario.reporter.report2UnformattedTable(text, System.out);
        }
        out.close();
        numFinishedInstances ++;
        if (numFinishedInstances == numInstances)
            IntegrationR.getInstance().end();
    }
    
    public final void perform(RuntimeContainer rc) {
         synchronized(rc.agent.lock){
            rc.increaseTick();
        }
    }

    public void init() {

        int n = get(Variable.NumberOfAgents).<Integer>value();
        try {
            Factory.config = config;

            //initing and setuping the network object            
            network = (Network) Factory.create(Network.TAG, Network.class.getName());
            network.init(this);
            Factory.setup(network, Network.TAG);

            set(Variable.Network, network);
            
            scenario = (Scenario) Factory.create(Scenario.TAG, Scenario.class.getName());
            scenario.init(this);
            Factory.setup(scenario, Scenario.TAG);
            
            network.setScenario(scenario);
            /*  #scenario
             *  included
             * 
             */
            
             try {
                scenario.initAgents();
                scenario.initChannels();
             } catch (Exception e) {
    		e.printStackTrace();
             }

            /*
              * #scenario
             
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
            
            */

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
       
    }

    /*
     *  #scenario
     *  remove method
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

        a.init();
    }
     
     */

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
        String WorkDir = configGeral.getString("workdir", ".");
        System.out.println("Configuring...");
        System.out.println(WorkDir);
        String modo = configGeral.getString("mode");
        
        String[] classNames =
            configGeral.getStringArray("scenes");
        System.out.println(classNames.length);
            if (classNames == null) {
                classNames = new String[0];
            }
        if (modo.equals("prototype")) {
            System.out.println("Running prototype, only first scene will be started...");
            int id = configGeral.getInteger("ID");
            System.out.println("agent #"+id);
            TestBed prot;
            java.util.Date data = new java.util.Date();
            System.out.println(data.toString());
            System.out.println("Starting prototyped scene: "+WorkDir+classNames[0]);
            config = getConfig(new String[] {WorkDir+classNames[0]});
            prot = new TestBed(config,WorkDir+classNames[0],id);
            prot.start();
        } 
        else {
            Simulator simulator;
            System.out.println("num = "+classNames.length);
            numInstances = classNames.length;
            numFinishedInstances = 0;
            for (int i = 0; i < classNames.length; i++) {
                config = getConfig(new String[] {WorkDir+classNames[i]});
                simulator = new Simulator(WorkDir+classNames[i]);
                simulator.start();
                simulator = null;
            }
        }
    }

    Simulator(String filename)
    {
        clock = 0;
        end = false;
        NAME = filename;

        try {
            out = new java.io.PrintStream(new java.io.FileOutputStream(filename+".out"));
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