package LoadLauncher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

import LoadLauncher.TraceManager;
//import LoadLauncher.Load.*;

public class Run {

	private static String PATH = "";
	private static String SCRIPT_NAME = "";
	private static File SCRIPT_FILE;
	private static File LOG_FILE;
	private static Load LOAD_SPEC;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int STRATEGY = -1;
		AgentID middleAgentID = null;
		
		SCRIPT_NAME = "process_results_";
		
		for (int nargs=0; nargs < args.length; nargs++){
			// Reading parameters
			String command=args[nargs];
			
			if (command.equals("bcast")){
				STRATEGY = Load.BROADCAST;
				SCRIPT_NAME = SCRIPT_NAME + "broadcast";
			}
			else if (command.equals("match")){
				STRATEGY = Load.MATCH_MAKER;
				SCRIPT_NAME = SCRIPT_NAME + "matchmaker";
			}
			else if (command.equals("broker")){
				STRATEGY = Load.BROKER;
				SCRIPT_NAME = SCRIPT_NAME + "broker";
			}
			else if (command.equals("trace")){
				STRATEGY = Load.EVENT_TRACE;
				SCRIPT_NAME = SCRIPT_NAME + "trace";
			}
			else if (command.equals("-i")){
				// input path
				nargs++;
				if (nargs < args.length){
					PATH=args[nargs]; 
				}
				else{
					System.out.println("Error: Input path expected");
				}
			}
		}
		
		switch(STRATEGY){
		
			case Load.BROADCAST:
				System.out.println("INITIALIZING BROADCAST...");
				middleAgentID = new AgentID("qpid://broadcastmanager@localhost:8080");
				break;
				
			case Load.MATCH_MAKER:
				System.out.println("INITIALIZING MATCH MAKER...");
				middleAgentID = new AgentID("qpid://matchmaker@localhost:8080");
				break;
				
			case Load.BROKER:
				System.out.println("INITIALIZING BROKER...");
				middleAgentID = new AgentID("qpid://broker@localhost:8080");
				break;
				
			case Load.EVENT_TRACE:
				System.out.println("INITIALIZING EVENT TRACE...");
				middleAgentID = new AgentID("qpid://tm@localhost:8080");
				break;
				
			default:
				System.out.println("UNDEFINED STRATEGY");
		}
		
		// Reading input cnf file and fill load_specs
		System.out.println("[MAIN] Loading " + PATH + "/cnf ...");
		LOAD_SPEC = new Load(PATH, middleAgentID, STRATEGY);
		System.out.println("[MAIN] Done!");
		
		// Creating results processor script
		SCRIPT_FILE = new File(PATH + "/" + SCRIPT_NAME);
		SCRIPT_FILE.setExecutable(true, false);
		BufferedWriter script_file;
		script_file = new BufferedWriter(new FileWriter(PATH + "/" + SCRIPT_NAME, false));
		
		System.out.println("[MAIN] Creating " + PATH + "/" + SCRIPT_NAME);
		
		script_file.write("#!/bin/bash\n\n");
		
		script_file.write("cat " + LOAD_SPEC.getOutPath() + "/" +
			Load.prefixes[LOAD_SPEC.getStrategy()] + "_coordinator_result_log.txt > " +
			LOAD_SPEC.getOutPath() + "/" + Load.prefixes[LOAD_SPEC.getStrategy()] + "_result_log.txt\n");

		switch(STRATEGY){
		
			case Load.BROADCAST:
				script_file.write("cat " + LOAD_SPEC.getOutPath() + "/bcast_broadcastmanager_result_log.txt > " +
					LOAD_SPEC.getOutPath() + "/" + Load.prefixes[LOAD_SPEC.getStrategy()] + "_result_log.txt\n");
				
				LOG_FILE = new File (PATH + "/bcast_broadcastmanager_result_log.txt");
				LOG_FILE.delete();
				
				break;
	
			case Load.MATCH_MAKER:
				script_file.write("cat " + LOAD_SPEC.getOutPath() + "/match_matchmaker_result_log.txt > " +
					LOAD_SPEC.getOutPath() + "/" + Load.prefixes[LOAD_SPEC.getStrategy()] + "_result_log.txt\n");
				
				LOG_FILE = new File (PATH + "/match_matchmaker_result_log.txt");
				LOG_FILE.delete();
				
				break;
	
			case Load.BROKER:
				script_file.write("cat " + LOAD_SPEC.getOutPath() + "/brokr_broker_result_log.txt > " +
						LOAD_SPEC.getOutPath() + "/" + Load.prefixes[LOAD_SPEC.getStrategy()] + "_result_log.txt\n");
				
				LOG_FILE = new File (PATH + "/brokr_broker_result_log.txt");
				LOG_FILE.delete();
				
				break;
	
			case Load.EVENT_TRACE:
				script_file.write("cat " + LOAD_SPEC.getOutPath() + "/trace_tracemanager_result_log.txt > " +
						LOAD_SPEC.getOutPath() + "/" + Load.prefixes[LOAD_SPEC.getStrategy()] + "_result_log.txt\n");
				
				LOG_FILE = new File (PATH + "/trace_tracemanager_result_log.txt");
				LOG_FILE.delete();
				
				break;
	
			default:
				System.out.println("[MAIN] UNDEFINED STRATEGY");
		}
		
		for (int i=0; i < LOAD_SPEC.N_PUBLISHERS; i++){
			script_file.write("cat " + LOAD_SPEC.getOutPath() + "/" + 
				Load.prefixes[LOAD_SPEC.getStrategy()] + "_publisher" + i + "_result_log.txt > " +
				LOAD_SPEC.getOutPath() + "/" + Load.prefixes[LOAD_SPEC.getStrategy()] + "_result_log.txt\n");
			
			LOG_FILE = new File (LOAD_SPEC.getOutPath() + "/" + 
				Load.prefixes[LOAD_SPEC.getStrategy()] + "_publisher" + i + "_result_log.txt");
			LOG_FILE.delete();
		}
		
		for (int i=0; i < LOAD_SPEC.N_SUBSCRIBERS; i++){
			script_file.write("cat " + LOAD_SPEC.getOutPath() + "/" + 
				Load.prefixes[LOAD_SPEC.getStrategy()] + "_subscriber" + i + "_result_log.txt > " +
				LOAD_SPEC.getOutPath() + "/" + Load.prefixes[LOAD_SPEC.getStrategy()] + "_result_log.txt\n");
			
			LOG_FILE = new File (LOAD_SPEC.getOutPath() + "/" + 
				Load.prefixes[LOAD_SPEC.getStrategy()] + "_subscriber" + i + "_result_log.txt");
			LOG_FILE.delete();
		}
		
		script_file.write("sort " + LOAD_SPEC.getOutPath() + "/" + SCRIPT_NAME + " -o " +
				LOAD_SPEC.getOutPath() + "/" + Load.prefixes[LOAD_SPEC.getStrategy()] + "_result_log.txt\n");
		
		script_file.close();
		
		// Launching test
		switch(STRATEGY){
		
			case Load.BROADCAST:
				launchBroadcast();
				break;
			
			case Load.MATCH_MAKER:
				launchMatchMaker();
				break;
			
			case Load.BROKER:
				launchBroker();
				break;
			
			case Load.EVENT_TRACE:
				launchEventTrace();
				break;
			
			default:
				System.out.println("[MAIN] UNDEFINED STRATEGY");
		}
	}
	
	private static void launchBroadcast(){
		PublisherBroadcast publishers[] = new PublisherBroadcast[LOAD_SPEC.N_PUBLISHERS];
		SubscriberBroadcast subscribers[] = new SubscriberBroadcast[LOAD_SPEC.N_SUBSCRIBERS];
		int i;
		
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			new BroadcastManager(LOAD_SPEC);
			
			System.out.println("LAUNCHING BROADCAST...");
			
			/**
			 * Instantiating the subscriber agents
			 */
			for (i=0; i < LOAD_SPEC.N_SUBSCRIBERS; i++){
				subscribers[i]=new SubscriberBroadcast(LOAD_SPEC, i);
			}
			
			/**
			 * Instantiating publisher agents
			 */
			for (i=0; i < LOAD_SPEC.N_PUBLISHERS; i++){
				publishers[i]=new PublisherBroadcast(LOAD_SPEC, i);
			}
			
			System.out.println("AGENTS LOADED (" + LOAD_SPEC.N_PUBLISHERS +
				" publishers vs " + LOAD_SPEC.N_SUBSCRIBERS + " subscribers)...");

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (i=0; i < LOAD_SPEC.N_SUBSCRIBERS; i++){
				subscribers[i].start();
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("SUBSCRIBERS STARTED...");
			
			for (i=0; i < LOAD_SPEC.N_PUBLISHERS; i++){
				publishers[i].start();
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("PUBLISHERS STARTED...");
			
//			System.out.println("Waiting " + (LOAD_SPEC.TEST_TIME+(LOAD_SPEC.N_PUBLISHERS+LOAD_SPEC.N_SUBSCRIBERS+LOAD_SPEC.N_CHANNELS)*1000)/1000 + " seconds...");
//			try {
//				Thread.sleep(LOAD_SPEC.TEST_TIME+(LOAD_SPEC.N_PUBLISHERS+LOAD_SPEC.N_SUBSCRIBERS+LOAD_SPEC.N_CHANNELS)*1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			
//			System.out.println("Done!");
			
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}
	
	private static void launchMatchMaker(){
		PublisherMatchMaker publishers[] = new PublisherMatchMaker[LOAD_SPEC.N_PUBLISHERS];
		SubscriberMatchMaker subscribers[] = new SubscriberMatchMaker[LOAD_SPEC.N_SUBSCRIBERS];
		int i;
		
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			new MatchMaker(LOAD_SPEC);
			
			System.out.println("LAUNCHING MATCHMAKER...");
			
			/**
			 * Instantiating publisher agents
			 */
			for (i=0; i < LOAD_SPEC.N_PUBLISHERS; i++){
				publishers[i]=new PublisherMatchMaker(LOAD_SPEC, i);
			}
			
			/**
			 * Instantiating the subscriber agents
			 */
			for (i=0; i < LOAD_SPEC.N_SUBSCRIBERS; i++){
				subscribers[i]=new SubscriberMatchMaker(LOAD_SPEC, i);
			}
			
			System.out.println("AGENTS LOADED (" + LOAD_SPEC.N_PUBLISHERS +
					" publishers vs " + LOAD_SPEC.N_SUBSCRIBERS + " subscribers)...");

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (i=0; i < LOAD_SPEC.N_SUBSCRIBERS; i++){
				subscribers[i].start();
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("SUBSCRIBERS STARTED...");
			
			for (i=0; i < LOAD_SPEC.N_PUBLISHERS; i++){
				publishers[i].start();
			}
			
			System.out.println("PUBLISHERS STARTED...");
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}
	
	private static void launchBroker(){
		PublisherBroker publishers[] = new PublisherBroker[LOAD_SPEC.N_PUBLISHERS];
		SubscriberBroker subscribers[] = new SubscriberBroker[LOAD_SPEC.N_SUBSCRIBERS];
		int i;

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			Broker broker = new Broker(LOAD_SPEC);
			broker.start();
			
			System.out.println("LAUNCHING BROKER...");
			
			/**
			 * Instantiating publisher agents
			 */
			for (i=0; i < LOAD_SPEC.N_PUBLISHERS; i++){
				publishers[i]=new PublisherBroker(LOAD_SPEC, i);
			}
			
			/**
			 * Instantiating the subscriber agents
			 */
			for (i=0; i < LOAD_SPEC.N_SUBSCRIBERS; i++){
				subscribers[i]=new SubscriberBroker(LOAD_SPEC, i);
			}
			
			System.out.println("AGENTS LOADED (" + LOAD_SPEC.N_PUBLISHERS +
					" publishers vs " + LOAD_SPEC.N_SUBSCRIBERS + " subscribers)...");
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (i=0; i < LOAD_SPEC.N_SUBSCRIBERS; i++){
				subscribers[i].start();
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("SUBSCRIBERS STARTED...");
			
			for (i=0; i < LOAD_SPEC.N_PUBLISHERS; i++){
				publishers[i].start();
			}
			
			System.out.println("PUBLISHERS STARTED...");
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}
	
	private static void launchEventTrace(){
		PublisherEventTrace publishers[] = new PublisherEventTrace[LOAD_SPEC.N_PUBLISHERS];
		SubscriberEventTrace subscribers[] = new SubscriberEventTrace[LOAD_SPEC.N_SUBSCRIBERS];
		int i;
		
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			/**
			 * Instantiating the Trace Manager
			 */
			new TraceManager(new AgentID("tm"), LOAD_SPEC);

			System.out.println("LAUNCHING EVENT TRACE...");
			
			/**
			 * Instantiating publisher agents
			 */
			for (i=0; i < LOAD_SPEC.N_PUBLISHERS; i++){
				publishers[i]=new PublisherEventTrace(LOAD_SPEC, i);
			}
			
			/**
			 * Instantiating the subscriber agents
			 */
			for (i=0; i < LOAD_SPEC.N_SUBSCRIBERS; i++){
				subscribers[i]=new SubscriberEventTrace(LOAD_SPEC, i);
			}
			
			System.out.println("AGENTS LOADED (" + LOAD_SPEC.N_PUBLISHERS +
					" publishers vs " + LOAD_SPEC.N_SUBSCRIBERS + " subscribers)...");
			
			for (i=0; i < LOAD_SPEC.N_SUBSCRIBERS; i++){
				subscribers[i].start();
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("SUBSCRIBERS STARTED...");
			
			for (i=0; i < LOAD_SPEC.N_PUBLISHERS; i++){
				publishers[i].start();
			}

			System.out.println("PUBLISHERS STARTED...");
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}
}
