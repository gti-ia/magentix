package Query_Example;


/**
 * Run class is an example of an agent that implements the FIPA Query protocol.
 *
 * 
 * 
 * @author Joan Bellver - jbellver@dsic.upv.es
 * @author Sergio Pajares - spajares@dsic.upv.es
 *
 */

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;




public class Run {

    /**
     * @param args
     */
    public static void main(String[] args) {


	// TODO Auto-generated method stub


	try{
	    AgentsConnection.connect();       // TODO add your handling code here:




	    Airport aeropuerto = new Airport(new AgentID("ManisesAirPort"));
	    aeropuerto.start();

	    Passenger viajante = new Passenger(new AgentID("Veronica"));
	    viajante.start();
	}catch(Exception e){}
    }

}
