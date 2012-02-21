package sfTest;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;

public class RunTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(RunTest.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
//			TesterAgentRegister testerAgent=new TesterAgentRegister(new AgentID("TesterAgent"));
//			testerAgent.start();
			
//			TesterAgentSearch testerAgentSearch=new TesterAgentSearch(new AgentID("TesterAgentSearch"));
//			testerAgentSearch.start();
			
//			TesterAgentGetService testerAgentGetService= new TesterAgentGetService(new AgentID("TesterAgentGetService"));
//			testerAgentGetService.start();
			
//			TesterAgentDeregister testerAgentDeregister = new TesterAgentDeregister(new AgentID("TesterAgentDeregister"));
//			testerAgentDeregister.start();
			
			TesterAgentRemove testerAgentRemove = new TesterAgentRemove(new AgentID("TesterAgentRemove"));
			testerAgentRemove.start();
			
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}
}
