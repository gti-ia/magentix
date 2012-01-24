package ThomasNOMindswap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.Oracle;

public class TestService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String token_process="http://localhost:8080/SearchCheapHotel/owl/owls/SearchCheapHotelProcess.owl";
//		List<String> params= new ArrayList<String>();
//		params.add("5");
//		params.add("Spain");
//		params.add("Valencia");
//		
//		ServiceClient serviceClient = new ServiceClient();
//	    ArrayList<String> results = serviceClient.invoke(token_process, params);
//		String resultStr="SearchCheapHotelProcess"+ "=" + "{";
//		for(int i=0;i<results.size();i++){
//			resultStr+=token_process+"#"+results.get(i);
//			if(i!=results.size()-1){
//				resultStr+=", ";
//			}
//			else{
//				resultStr+="}";
//			}
//		}
//		
//		 System.out.println("[Provider] "+resultStr);
		 
		 
		String processURL="http://localhost:8080/omsservices/OMSservices/owl/owls/AcquireRoleProcess.owl";
		Oracle oracle = new Oracle();
		oracle.setURLProcess(processURL); 
		
		 
		 
//		 DOMConfigurator.configure("configuration/loggin.xml");
//		Logger logger = Logger.getLogger(TestService.class);
//
//		/**
//		 * Connecting to Qpid Broker, default localhost.
//		 */
//		AgentsConnection.connect();
//
//
//		/**
//		 * Clean database
//		 */
//
//		CleanDB clean = new CleanDB();
//
//
//		
//		clean.initialize_db();
//
//
//
//		try {
//
//			/**
//			 * Instantiating a OMS and FS agent's
//			 
//
//			OMS agenteOMS = OMS.getOMS();
//			agenteOMS.start();
//
//			SF agenteSF = SF.getSF();
//			agenteSF.start();
//			**/
//
//			/**
//			 * Execute the agents
//			 */
//
////				AgentPayee payeeAgent = new AgentPayee(new AgentID("agentPayee"));
//
//			TestAgent providerAgent = new TestAgent(new AgentID("providerAgent"));
//
////			AgentAnnouncement registerAgent = new AgentAnnouncement(new AgentID("registerAgent"));
//
////			AgentClient clientAgent = new AgentClient(new AgentID("clientAgent"));
//
////			registerAgent.start();
//////				payeeAgent.start();
////
////			Monitor m = new Monitor();
////			m.waiting(5 * 1000);
//			providerAgent.start();
////			m.waiting(5 * 1000);
////
////			clientAgent.start();
//
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//
//		}
	
	
	
	
	}

}
