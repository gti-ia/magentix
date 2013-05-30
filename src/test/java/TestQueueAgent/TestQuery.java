package TestQueueAgent;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Ignore;
import org.junit.Test;


import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import junit.framework.TestCase;

/**
 * Test class for an example of QueueAgent, Qusing the FIPA Query Protocol
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */


public class TestQuery extends TestCase{
	
	Airport airport;
	Passenger passenger;
	Logger logger;
	
	public TestQuery(String name){
		super(name);
	}
	
	public void setUp() throws Exception{
		super.setUp();
		
		try {
		
			/**
			 * Setting the configuration
			 */
			DOMConfigurator.configure("configuration/loggin.xml");
				
			/**
			* Connecting to Qpid Broker, default localhost.
			*/	
		    AgentsConnection.connect();
		        
		    /**
			 * Instantiating an Airport agent
			 */
			airport = new Airport(new AgentID("ManisesAirPort"));
			
	
			/**
			 * Instantiating a Passenger agent
			 */
			passenger = new Passenger(new AgentID("Veronica"));
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	/*
	
	/**
	 * Testing Airport agree answer
	 */
	public void testAgreeAnswer(){
		airport.ASSIST_PROB=0.4;//They will assist the passenger
		airport.start();
		passenger.start();
		
		//If passenger has not received answer wait
		while(passenger.queryResult.equalsIgnoreCase("") || !airport.finished() || !passenger.finished())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		
		assertEquals("Wait a moment please, we are looking for in the Database" + airport.getName()
				,passenger.queryResult);
	}
	/**
	 * Testing Airport refuse answer
	 */
	public void testRefuseAnswer(){
		System.out.println("Comenzando 2º test");
		airport.ASSIST_PROB=0.5;//They will not assist the passenger
		airport.start();
		passenger.start();
		
		//If passenger has not received answer wait
		while(passenger.queryResult.equalsIgnoreCase("") || !airport.finished() || !passenger.finished())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		
		assertEquals(passenger.getName()+": At the moment all operators are busy. We can not assist"+airport.getName()
				,passenger.queryResult);
	}
	
	/**
	 * Testing Airport successfull reservation
	 */
	public void testSuccesfullReservation(){
		//Reservation will be successfull as "Veronica" has more than 5 caracters
		airport.ASSIST_PROB=0.4;//They will not assist the passenger
		airport.start();
		passenger.start();
		
		//If passenger has not received answer wait
		while(passenger.queryResult.equalsIgnoreCase("") || !airport.finished())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
		}
		
		assertEquals("The operator reports:You have made a reservation",passenger.informResult);
	}
	
	/**
	 * Testing Airport unsuccessfull reservation
	 * @throws Exception 
	 */
	public void testUnsuccesfullReservation() throws Exception{
		airport.ASSIST_PROB=0.4;//They will assist the passenger
		passenger = new Passenger(new AgentID("Ana"));
		//Reservation will be successfull as "Ana" has more than 5 caracters
		airport.start();
		passenger.start();
		
		//If passenger has not received answer wait
		while(passenger.queryResult.equalsIgnoreCase("") || !airport.finished())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		
		assertEquals("The operator reports:You have no reserves",passenger.informResult);
	}
}
