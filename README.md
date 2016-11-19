# HDDSS - Hybrid and Dynamic Distributed Systems Simulator

This project is a framework that allows to evaluate distributed algorithms through simulation.

INSTALL GUIDE

I) On Debian/Ubuntu Linux:

1) Install a JAVA environment:

	sudo apt-get install openjdk-7-*
	sudo R CMD javareconf

2) Install R environment, according to https://www.r-project.org/

3) install rJava in R via R command line:

	install.packages("rJava").

4) Do some hacks to adjust environment variables (you should check the correct path to each case): 

	ln -s /usr/local/lib/R/site-library/rJava/jri/libjri.so /usr/local/lib/libjri.so

	export R_HOME=/usr/lib/R
	export CLASSPATH=.:/usr/local/lib/R/site-library/rJava/jri
	export LD_LIBRARY_PATH=/usr/local/lib/R/site-library/rJava/jri:/usr/lib/R/lib:/usr/lib

5) Run a test simulation, from hddss base directory:

	java -Djava.library.path=.:/usr/local/lib/R/site-library/rJava/jri -jar dist/jds.jar examples/config-timed-20.txt

II) On Windows


1) Install a JAVA environment, according to http://java.sun.com/

2) Install R environment, according to https://www.r-project.org/

3) install rJava in R via R command line:

	install.packages("rJava").

4) Do some hacks to adjust environment variables (you should check the correct path to each case): 

	In Computer - Properties - Advanced ... Environment Variables set 
		(a) R_HOME to the value indicated by R command R.home()
		(b) CLASSPATH to reference current directory and rJava folder, example: ".;C:\Users\ads\Documents\R\win-library\3.2\rJava\jri"		
		(c) JAVA_HOME to local of your java instalation, example "C:\Progra~1\Java\jdk1.8.0_51"
	
		(d) PATH to add R and rJava binaries, example: 	"<OLDPATH>;C:\Users\myUser\Documents\R\win-library\3.2\rJava\jri\i386;C:\Program Files\R\R-3.2.1\bin\i386"

5) Run a test simulation, from hddss base directory:

	java -jar dist/jds.jar examples/config-timed-20.txt

III) Instalation Notes

1) Structure of configuration file:

workdir = /repositorio/hddss/jds/examples/amoeba/ 	--> it references to the working directory (that is, the folder where scenario files are)
scenes = amoebaAC-80-10-HIGH.txt 			--> name of scenario files separated by commas
mode = simulation  					--> simulation for a simulation or prototype for running at real environment (prototype is broken for now)

2) Structure of a simulation file:

	FinalTime = 6000					--> time units of simulation
	NumberOfAgents = 3					--> number of agents running
	MaximumDeviation = 4					--> max deviation for clocks (that is \rho)
	Mode = clock						--> set to clock for clocks running according \rho 
	Debug = true						--> debugging or not (true or false)
	FormattedReport = false					--> presenting a human-readable report or a machine one (true or false)

	clock = br.ufba.lasid.jds.prototyping.hddss.Clock_Virtual	--> class for clock
	clock.Mode = s							--> properties of that class

	cpu = br.ufba.lasid.jds.prototyping.hddss.LoadAwareCPU		--> class for processors
	cpu.ProcessingRate = 9446400 					--> properties of that class
	cpu.LoadCost = 0.000000001

	agent = br.ufba.lasid.jds.prototyping.hddss.Agent_AmoebaSequencer	--> class for agent
	agent.DeltaMax = 100							--> properties of that class
	agent.TS = 200
	agent.PacketGenerationProb = 0.15

	channel = br.ufba.lasid.jds.prototyping.hddss.ChannelLogNormal		--> class for communication channels between agents
	channel.Mean = 10							--> properties of that class
	channel.MinDelay = 1
	channel.Std = 5

	network = br.ufba.lasid.jds.prototyping.hddss.NetworkDeterministic	--> class for networking
	network.ProcessingTime = .001						--> properties of that class
	network.FIFO = true
	network.TripBalance = 0.5

3) Useful notes

For running that test simulation we assume hddss is under a repositorio folder at the root of file system, that may be acomplished by using symbolic links, or you may change configuration files.

HDDSS was developed on Netbeans. You may use that IDE or one of your choice to develop your simulations.

HDDSS runs a bundle of simulation environments, listed on a config file that references text files related to each simulated environment.

You may use index (from 0 to n-1) to refer to specific agent, ex: agent[0], setting different class and properties.

You may use index (from 0 to n-1) to define different types of communication channels, a scenario will configure what channel will be stablished between each agent (and how that changes during simulation), example:

	scenario = br.ufba.lasid.jds.prototyping.hddss.Scenario_Spa
	scenario.NumberOfAgentsPerType = 4 1 4 1 4 1
	scenario.NumberOfAgentsPerPartition = 5 5 5
	scenario.SynchronousChannelType = 0
	scenario.AsynchronousChannelType = 1

	channel[0] = br.ufba.lasid.jds.prototyping.hddss.ChannelDeterministicInterval
	channel[0].DeltaMinimo = 5
	channel[0].DeltaMaximo = 10

	channel[1]= br.ufba.lasid.jds.prototyping.hddss.ChannelProbabilistic
	channel[1].MinValue = 1
	channel[1].Distribution = normal(10.0,5.0)

A full reference of HDDSS and a discussion on distributed systems simulation may be found in this PhD Thesis [4] - Portuguese only -, or in this paper [5].

KNOWN BUGS: setting true to FormattedReport option on a simulation file.

REFERENCES:

[1] Talking r through Java. Available at http://binfalse.de/2011/02/20/talking-r-through-java/ (A useful reference for rJava integration on Linux)

[2] rJava - Low-level R to Java interface. Available at https://www.rforge.net/rJava/index.html

[3] The R Project for Statistical Computing. Available at https://www.r-project.org/

[4] FREITAS, A. E. S. "Simulação de Sistemas Distribuídos Híbridos e Dinâmicos". PhD Thesis. Computer Science Department. Federal University of Bahia. 2013.

[5] FREITAS, A. E. S. and MACEDO, R. J. A. "A performance evaluation tool for hybrid and dynamic distributed systems". Operating Systems Review, v. 48, p. 11-18, 2014. Available at http://dx.doi.org/10.1145/2626401.2626404 
